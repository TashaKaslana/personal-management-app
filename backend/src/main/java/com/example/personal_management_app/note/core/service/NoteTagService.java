package com.example.personal_management_app.note.core.service;

import lombok.RequiredArgsConstructor;
import com.example.personal_management_app.note.core.exception.NoteNotFoundException;
import com.example.personal_management_app.note.core.exception.NoteTagAlreadyAssignedException;
import com.example.personal_management_app.note.core.exception.NoteTagNotAssignedException;
import com.example.personal_management_app.note.core.infrastructure.persistence.entities.Note;
import com.example.personal_management_app.note.core.infrastructure.persistence.entities.NoteTag;
import com.example.personal_management_app.note.core.infrastructure.persistence.key.NoteTagId;
import com.example.personal_management_app.note.core.infrastructure.persistence.repositories.NoteRepository;
import com.example.personal_management_app.note.core.infrastructure.persistence.repositories.NoteTagRepository;
import com.example.personal_management_app.note.tag.enums.TagError;
import com.example.personal_management_app.note.tag.exception.TagNotFoundException;
import com.example.personal_management_app.note.tag.infrastructure.persistence.entities.Tag;
import com.example.personal_management_app.note.tag.infrastructure.persistence.repositories.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages the note <-> tag join table (explicit NoteTag entity, following the RolePermission pattern)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteTagService {

    private final NoteTagRepository noteTagRepository;
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    /**
     * Assign tag to note
     */
    @Transactional
    public void assignTagToNote(UUID noteId, UUID tagId) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new NoteNotFoundException(noteId.toString()));

        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new TagNotFoundException(tagId.toString()));

        NoteTagId id = new NoteTagId();
        id.setNoteId(noteId);
        id.setTagId(tagId);

        // Check if the assignment already exists
        if (noteTagRepository.existsById(id)) {
            throw new NoteTagAlreadyAssignedException(noteId.toString(), tagId.toString());
        }

        NoteTag noteTag = new NoteTag();
        noteTag.setId(id);
        noteTag.setNote(note);
        noteTag.setTag(tag);

        noteTagRepository.save(noteTag);
    }

    /**
     * Remove tag from note
     */
    @Transactional
    public void removeTagFromNote(UUID noteId, UUID tagId) {
        NoteTagId id = new NoteTagId();
        id.setNoteId(noteId);
        id.setTagId(tagId);

        if (!noteTagRepository.existsById(id)) {
            throw new NoteTagNotAssignedException(noteId.toString(), tagId.toString());
        }

        noteTagRepository.deleteById(id);
    }

    /**
     * Find note-tag link by composite ID
     */
    public Optional<NoteTag> findById(UUID noteId, UUID tagId) {
        NoteTagId id = new NoteTagId();
        id.setNoteId(noteId);
        id.setTagId(tagId);

        return noteTagRepository.findById(id);
    }

    /**
     * Find all tags attached to a note
     */
    public List<Tag> findTagsByNote(UUID noteId) {
        return noteTagRepository.findByIdNoteId(noteId).stream()
            .map(NoteTag::getTag)
            .toList();
    }

    /**
     * Bulk: group tags by note ID (used to enrich note lists without N+1 queries)
     */
    public Map<UUID, List<Tag>> findTagsByNoteIds(List<UUID> noteIds) {
        if (noteIds == null || noteIds.isEmpty()) {
            return Map.of();
        }
        return noteTagRepository.findByIdNoteIdIn(noteIds).stream()
            .collect(Collectors.groupingBy(
                noteTag -> noteTag.getId().getNoteId(),
                Collectors.mapping(NoteTag::getTag, Collectors.toList())
            ));
    }

    /**
     * Find all (non-deleted) notes that have a specific tag
     */
    public List<Note> findActiveNotesByTag(UUID tagId) {
        return noteRepository.findActiveByTagId(tagId);
    }

    /**
     * Check if a tag is assigned to a note
     */
    public boolean isTagAssignedToNote(UUID noteId, UUID tagId) {
        NoteTagId id = new NoteTagId();
        id.setNoteId(noteId);
        id.setTagId(tagId);

        return noteTagRepository.existsById(id);
    }

    /**
     * Count non-deleted notes linked to a tag
     */
    public long countActiveNotesByTag(UUID tagId) {
        return noteRepository.countActiveByTagId(tagId);
    }

    /**
     * Count all note links for a tag (including trashed notes)
     */
    public long countNoteLinksByTag(UUID tagId) {
        return noteTagRepository.countByIdTagId(tagId);
    }

    /**
     * Replace the full tag set of a note with the given tag ids.
     * A tag that does not exist is an error; an empty list clears all tags.
     */
    @Transactional
    public void replaceTags(UUID noteId, List<UUID> tagIds) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new NoteNotFoundException(noteId.toString()));

        List<UUID> distinctTagIds = tagIds.stream().distinct().toList();

        if (distinctTagIds.isEmpty()) {
            noteTagRepository.deleteAllByIdNoteId(noteId);
            return;
        }

        // Verify all tags exist
        List<Tag> tags = tagRepository.findByIdIn(distinctTagIds);
        if (tags.size() != distinctTagIds.size()) {
            throw new TagNotFoundException(TagError.TAG_LIST_NOT_FOUND, distinctTagIds.toString());
        }
        Map<UUID, Tag> tagsById = tags.stream()
            .collect(Collectors.toMap(Tag::getId, tag -> tag));

        Set<UUID> existingTagIds = noteTagRepository.findByIdNoteId(noteId).stream()
            .map(noteTag -> noteTag.getId().getTagId())
            .collect(Collectors.toSet());

        // Add missing links
        List<NoteTag> toAdd = distinctTagIds.stream()
            .filter(tagId -> !existingTagIds.contains(tagId))
            .map(tagId -> {
                NoteTagId id = new NoteTagId();
                id.setNoteId(noteId);
                id.setTagId(tagId);

                NoteTag noteTag = new NoteTag();
                noteTag.setId(id);
                noteTag.setNote(note);
                noteTag.setTag(tagsById.get(tagId));
                return noteTag;
            })
            .toList();
        if (!toAdd.isEmpty()) {
            noteTagRepository.saveAll(toAdd);
        }

        // Remove links that are no longer wanted
        List<NoteTag> toRemove = noteTagRepository.findByIdNoteId(noteId).stream()
            .filter(noteTag -> !distinctTagIds.contains(noteTag.getId().getTagId()))
            .toList();
        if (!toRemove.isEmpty()) {
            noteTagRepository.deleteAll(toRemove);
        }
    }

    /**
     * Remove all tag links from a note (used when a note is permanently deleted)
     */
    @Transactional
    public int removeAllTagsFromNote(UUID noteId) {
        return noteTagRepository.deleteAllByIdNoteId(noteId);
    }

    /**
     * Bulk: remove all tag links from multiple notes (used by trash purge / empty trash)
     */
    @Transactional
    public int removeAllTagsFromNotes(List<UUID> noteIds) {
        if (noteIds == null || noteIds.isEmpty()) {
            return 0;
        }
        return noteTagRepository.deleteAllByIdNoteIdIn(noteIds);
    }

    /**
     * Remove all note links from a tag (used by clearTag / deleteTag)
     */
    @Transactional
    public int removeAllNoteLinksFromTag(UUID tagId) {
        return noteTagRepository.deleteAllByIdTagId(tagId);
    }

    /**
     * Count all note-tag links
     */
    public long count() {
        return noteTagRepository.count();
    }
}
