package com.example.personal_management_app.note.core.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import com.example.personal_management_app.note.collaboration.enums.CollaboratorRole;
import com.example.personal_management_app.note.core.dtos.UpdateNoteRequest;
import com.example.personal_management_app.note.collaboration.service.NoteMembershipService;
import com.example.personal_management_app.note.core.exception.StaleNoteVersionException;
import com.example.personal_management_app.core.security.CurrentUserProvider;
import com.example.personal_management_app.note.core.exception.NoteAccessDeniedException;
import com.example.personal_management_app.note.core.dtos.CreateNoteRequest;
import com.example.personal_management_app.note.core.dtos.NoteDto;
import com.example.personal_management_app.note.reminder.enums.ReminderRepeat;
import com.example.personal_management_app.note.core.exception.NoteNotFoundException;
import com.example.personal_management_app.note.core.infrastructure.mapstruct.NoteMapper;
import com.example.personal_management_app.note.core.infrastructure.persistence.entities.Note;
import com.example.personal_management_app.note.core.infrastructure.persistence.repositories.NoteRepository;
import com.example.personal_management_app.note.tag.infrastructure.mapstruct.TagMapper;
import com.example.personal_management_app.note.tag.infrastructure.persistence.entities.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.transaction.annotation.Propagation;
import java.util.UUID;

/**
 * Core note CRUD + lifecycle operations (pin / archive / trash / reminder), data only.
 * Trash retention: soft-deleted notes are purged after {@link #TRASH_RETENTION_DAYS} days
 * (call {@link #purgeExpiredTrash()} - e.g. from a Quartz job - or POST /notes/trash/purge).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteService {

    public static final int TRASH_RETENTION_DAYS = 30;

    private final EntityManager entityManager;
    private final NoteMembershipService memberships;
    private final NoteRepository noteRepository;
    private final CurrentUserProvider currentUserProvider;
    private final NoteTagService noteTagService;
    private final NoteSearchTextExtractor noteSearchTextExtractor;
    private final NoteMapper noteMapper;
    private final TagMapper tagMapper;

    /**
     * Create a new note using DTO
     */
    @Transactional
    public NoteDto createNote(CreateNoteRequest request) {
        getActorOrThrow();
        Note note = noteMapper.toEntity(request);
        note.setSearchText(noteSearchTextExtractor.extract(request.getTitle(), request.getContent()));

        Note savedNote = noteRepository.saveAndFlush(note);

        if (request.getTagIds() != null) {
            noteTagService.replaceTags(savedNote.getId(), request.getTagIds());
        }

        return getNoteById(savedNote.getId());
    }

    /**
     * Create multiple notes in bulk
     */
    @Transactional
    public List<NoteDto> createNotes(List<CreateNoteRequest> requests) {
        return requests.stream()
            .map(this::createNote)
            .toList();
    }

    /**
     * Find a note by ID
     */
    public NoteDto getNoteById(UUID id) {
        Note note = getNoteEntity(id);
        if (!visible(note)) {
            throw new NoteNotFoundException(id.toString());
        }
        return toDtoWithTags(note);
    }

    /**
     * Find all non-deleted notes (active + archived, pinned first)
     */
    public List<NoteDto> findAllActive() {
        return toDtoListWithTags(noteRepository.findAllActive(getActorOrThrow()));
    }

    /**
     * Find all non-deleted, non-archived notes (the main note grid)
     */
    public List<NoteDto> findAllUnarchived() {
        return toDtoListWithTags(noteRepository.findAllUnarchived(getActorOrThrow()));
    }

    /**
     * Find all archived notes (not in trash)
     */
    public List<NoteDto> findAllArchived() {
        return toDtoListWithTags(noteRepository.findAllArchived(getActorOrThrow()));
    }

    /**
     * Find all pinned notes (not in trash)
     */
    public List<NoteDto> findAllPinned() {
        return toDtoListWithTags(noteRepository.findAllPinned(getActorOrThrow()));
    }

    /**
     * Find all notes with pagination
     */
    public Page<NoteDto> findAll(Pageable pageable) {
        return noteRepository.findVisible(getActorOrThrow(), pageable)
            .map(noteMapper::toDto);
    }

    /**
     * Find trashed notes, optionally filtered by a query (visible text or tag name)
     */
    public List<NoteDto> findAllTrashed(String query) {
        List<Note> trashed = (query == null || query.isBlank())
            ? noteRepository.findAllTrashed(getActorOrThrow())
            : noteRepository.searchTrashed(getActorOrThrow(), query.trim());

        return toDtoListWithTags(trashed);
    }

    /**
     * Search non-deleted notes by visible text (title + block text) or tag name
     */
    public List<NoteDto> searchActive(String query) {
        if (query == null || query.isBlank()) {
            return findAllActive();
        }

        return toDtoListWithTags(noteRepository.searchActive(getActorOrThrow(), query.trim()));
    }

    /**
     * Find all non-deleted notes that have the given tag
     */
    public List<NoteDto> findActiveByTagId(UUID tagId) {
        return toDtoListWithTags(noteTagService.findActiveNotesByTag(tagId));
    }

    /**
     * Update a note (full replace). A null {@code tagIds} keeps the existing tags.
     */
    @Transactional
    public NoteDto updateNote(UUID id, UpdateNoteRequest request) {
        Note note = getNoteEntity(id);
        boolean owner = getActorOrThrow().equals(note.getCreatedBy());
        if (!owner && (note.getDeletedAt() != null || !memberships.hasRole(id, getActorOrThrow(), CollaboratorRole.EDITOR))) {
            throw new NoteAccessDeniedException("Editing this note requires editor access");
        }
        if (request.getVersion() == null || request.getVersion() != note.getVersion()) {
            throw new StaleNoteVersionException();
        }
        if (owner) {
            noteMapper.updateEntity(request, note);
        } else {
            noteMapper.updateEditorFields(request, note);
        }
        note.setSearchText(noteSearchTextExtractor.extract(request.getTitle(), request.getContent()));

        if (owner && request.getTagIds() != null) {
            noteTagService.replaceTags(id, request.getTagIds());
        }

        Note savedNote = noteRepository.saveAndFlush(note);
        return toDtoWithTags(savedNote);
    }

    /**
     * Soft delete a note (move to trash). Unpins and unarchives it, mirroring the frontend behavior.
     */
    @Transactional
    public void deleteNote(UUID id) {
        Note note = getNoteEntity(id);
        requireOwner(note);

        note.setDeletedAt(OffsetDateTime.now());
        note.setPinned(false);
        note.setArchivedAt(null);
        noteRepository.save(note);
    }

    /**
     * Soft delete multiple notes in bulk (move to trash)
     */
    @Transactional
    public void deleteNotes(List<UUID> ids) {
        ids.forEach(this::deleteNote);
    }

    /**
     * Restore a note from trash
     */
    @Transactional
    public NoteDto restoreNote(UUID id) {
        Note note = getNoteEntity(id);
        requireOwner(note);

        note.setDeletedAt(null);
        Note savedNote = noteRepository.saveAndFlush(note);
        return toDtoWithTags(savedNote);
    }

    /**
     * Permanently delete a note (also removes its tag links)
     */
    @Transactional
    public void deleteNotePermanently(UUID id) {
        Note note = getNoteEntity(id);
        requireOwner(note);

        noteTagService.removeAllTagsFromNote(id);
        noteRepository.delete(note);
    }

    /**
     * Permanently delete all trashed notes. Returns the number of purged notes.
     */
    @Transactional
    public int emptyTrash() {
        List<Note> trashed = noteRepository.findAllTrashed(getActorOrThrow());
        if (trashed.isEmpty()) {
            return 0;
        }

        List<UUID> noteIds = trashed.stream().map(Note::getId).toList();
        noteTagService.removeAllTagsFromNotes(noteIds);
        noteRepository.deleteAllById(noteIds);
        return trashed.size();
    }

    /**
     * Permanently delete notes that have been in the trash longer than
     * {@link #TRASH_RETENTION_DAYS}. Returns the number of purged notes.
     */
    @Transactional
    public int purgeExpiredTrash() {
        OffsetDateTime threshold = OffsetDateTime.now().minusDays(TRASH_RETENTION_DAYS);
        List<Note> expired = noteRepository.findByCreatedByAndDeletedAtBefore(getActorOrThrow(), threshold);
        if (expired.isEmpty()) {
            return 0;
        }

        List<UUID> noteIds = expired.stream().map(Note::getId).toList();
        noteTagService.removeAllTagsFromNotes(noteIds);
        noteRepository.deleteAllById(noteIds);

        return expired.size();
    }

    /**
     * Pin or unpin a note
     */
    @Transactional
    public NoteDto setPinned(UUID id, boolean pinned) {
        Note note = getNoteEntity(id);
        requireOwner(note);

        note.setPinned(pinned);
        Note savedNote = noteRepository.saveAndFlush(note);

        return toDtoWithTags(savedNote);
    }

    /**
     * Archive or unarchive a note (independent of the trash state)
     */
    @Transactional
    public NoteDto setArchived(UUID id, boolean archived) {
        Note note = getNoteEntity(id);
        requireOwner(note);

        note.setArchivedAt(archived ? OffsetDateTime.now() : null);
        Note savedNote = noteRepository.saveAndFlush(note);

        return toDtoWithTags(savedNote);
    }

    /**
     * Set a reminder on a note. A null {@code repeat} defaults to ONCE.
     */
    @Transactional
    public NoteDto setReminder(UUID id, OffsetDateTime at, ReminderRepeat repeat) {
        Note note = getNoteEntity(id);
        requireOwner(note);

        note.setReminderAt(at);
        note.setReminderRepeat(at != null ? (repeat != null ? repeat : ReminderRepeat.ONCE) : null);
        Note savedNote = noteRepository.saveAndFlush(note);

        return toDtoWithTags(savedNote);
    }

    /**
     * Clear the reminder of a note
     */
    @Transactional
    public NoteDto clearReminder(UUID id) {
        Note note = getNoteEntity(id);
        requireOwner(note);

        note.setReminderAt(null);
        note.setReminderRepeat(null);
        Note savedNote = noteRepository.saveAndFlush(note);

        return toDtoWithTags(savedNote);
    }

    public List<UUID> findDueReminderIds(OffsetDateTime now) {
        return noteRepository.findDueReminderIds(now);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<NoteDto> lockDueReminder(UUID id, OffsetDateTime now) {
        return noteRepository.lockDueReminder(id, now).map(noteMapper::toDto);
    }

    /** Advance the occurrence while the delivery transaction still owns the note lock. */
    @Transactional(propagation = Propagation.MANDATORY)
    public void advanceLockedReminder(UUID id, OffsetDateTime next) {
        Note note = getNoteEntity(id);
        note.setReminderAt(next);
        if (next == null) {
            note.setReminderRepeat(null);
        }
    }

    private UUID getActorOrThrow() {
        return currentUserProvider.getCurrentUserId()
                .filter(id -> !new UUID(0, 0).equals(id))
                .orElseThrow(() -> new NoteAccessDeniedException("Sign in to access notes"));
    }

    private boolean visible(Note note) {
        UUID userId = getActorOrThrow();
        return userId.equals(note.getCreatedBy())
                || (note.getDeletedAt() == null && memberships.contains(note.getId(), userId));
    }

    public void requireOwner(UUID noteId) {
        requireOwner(getNoteEntity(noteId));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public UUID lockOwnedNoteForSharing(UUID noteId) {
        Note note = getNoteEntity(noteId);
        requireOwner(note);
        entityManager.lock(note, LockModeType.PESSIMISTIC_WRITE);
        return note.getCreatedBy();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void advanceSharingVersion(UUID noteId) {
        Note note = getNoteEntity(noteId);
        requireOwner(note);
        entityManager.lock(note, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
    }

    private void requireOwner(Note note) {
        if (!getActorOrThrow().equals(note.getCreatedBy())) {
            throw new NoteAccessDeniedException("Only the note owner can perform this action");
        }
    }

    /**
     * Check if a note exists
     */
    public boolean existsById(UUID id) {
        return noteRepository.findById(id).filter(this::visible).isPresent();
    }

    /**
     * Count all notes (including trashed)
     */
    public long count() {
        return noteRepository.countVisible(getActorOrThrow());
    }

    /**
     * Count non-deleted notes
     */
    public long countActive() {
        return noteRepository.countByDeletedAtIsNull(getActorOrThrow());
    }

    /**
     * Count archived notes (not in trash)
     */
    public long countArchived() {
        return noteRepository.countByDeletedAtIsNullAndArchivedAtIsNotNull(getActorOrThrow());
    }

    /**
     * Count trashed notes
     */
    public long countTrashed() {
        return noteRepository.countByDeletedAtIsNotNull(getActorOrThrow());
    }

    /**
     * Count non-deleted notes linked to a tag
     */
    public long countActiveByTagId(UUID tagId) {
        return noteRepository.countActiveByTagId(getActorOrThrow(), tagId);
    }

    // Internal method for getting entity (used by other services)
    Note getNoteEntity(UUID id) {
        return noteRepository.findById(id)
            .orElseThrow(() -> new NoteNotFoundException(id.toString()));
    }

    private NoteDto toDtoWithTags(Note note) {
        List<Tag> tags = noteTagService.findTagsByNote(note.getId());
        return noteMapper.toDto(note, tagMapper.toDtoList(tags));
    }

    private List<NoteDto> toDtoListWithTags(List<Note> notes) {
        if (notes.isEmpty()) {
            return List.of();
        }

        Map<UUID, List<Tag>> tagsByNoteId = noteTagService.findTagsByNoteIds(
            notes.stream().map(Note::getId).toList()
        );

        return notes.stream()
            .map(note -> noteMapper.toDto(note, tagMapper.toDtoList(tagsByNoteId.getOrDefault(note.getId(), List.of()))))
            .toList();
    }
}
