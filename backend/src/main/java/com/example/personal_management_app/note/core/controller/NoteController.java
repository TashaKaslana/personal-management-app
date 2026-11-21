package com.example.personal_management_app.note.core.controller;

import lombok.RequiredArgsConstructor;
import com.example.personal_management_app.core.responses.RestApiResponse;
import com.example.personal_management_app.note.core.dtos.CreateNoteRequest;
import com.example.personal_management_app.note.core.dtos.NoteDto;
import com.example.personal_management_app.note.core.dtos.SetArchiveStatusRequest;
import com.example.personal_management_app.note.core.dtos.SetNoteTagsRequest;
import com.example.personal_management_app.note.core.dtos.SetPinStatusRequest;
import com.example.personal_management_app.note.core.dtos.SetReminderRequest;
import com.example.personal_management_app.note.core.service.NoteService;
import com.example.personal_management_app.note.core.service.NoteTagService;
import com.example.personal_management_app.note.tag.dtos.TagDto;
import com.example.personal_management_app.note.tag.infrastructure.mapstruct.TagMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    private final NoteTagService noteTagService;
    private final TagMapper tagMapper;

    // --- CRUD ---

    @PostMapping
    public ResponseEntity<RestApiResponse<NoteDto>> createNote(@Valid @RequestBody CreateNoteRequest request) {
        NoteDto createdNote = noteService.createNote(request);
        return RestApiResponse.created(createdNote, "Note created successfully");
    }

    @PostMapping("/bulk")
    public ResponseEntity<RestApiResponse<List<NoteDto>>> createNotes(@Valid @RequestBody List<CreateNoteRequest> requests) {
        List<NoteDto> createdNotes = noteService.createNotes(requests);
        return RestApiResponse.created(createdNotes, "Notes created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<NoteDto>> getNoteById(@PathVariable UUID id) {
        NoteDto note = noteService.getNoteById(id);
        return RestApiResponse.success(note, "Note retrieved successfully");
    }

    /**
     * All non-deleted notes (active + archived), pinned first.
     */
    @GetMapping
    public ResponseEntity<RestApiResponse<List<NoteDto>>> getAllNotes() {
        List<NoteDto> notes = noteService.findAllActive();
        return RestApiResponse.success(notes, "Notes retrieved successfully");
    }

    /**
     * Non-deleted, non-archived notes (the main note grid).
     */
    @GetMapping("/unarchived")
    public ResponseEntity<RestApiResponse<List<NoteDto>>> getUnarchivedNotes() {
        List<NoteDto> notes = noteService.findAllUnarchived();
        return RestApiResponse.success(notes, "Notes retrieved successfully");
    }

    @GetMapping("/archived")
    public ResponseEntity<RestApiResponse<List<NoteDto>>> getArchivedNotes() {
        List<NoteDto> notes = noteService.findAllArchived();
        return RestApiResponse.success(notes, "Archived notes retrieved successfully");
    }

    @GetMapping("/pinned")
    public ResponseEntity<RestApiResponse<List<NoteDto>>> getPinnedNotes() {
        List<NoteDto> notes = noteService.findAllPinned();
        return RestApiResponse.success(notes, "Pinned notes retrieved successfully");
    }

    @GetMapping("/paginated")
    public ResponseEntity<RestApiResponse<List<NoteDto>>> getAllNotes(Pageable pageable) {
        Page<NoteDto> notes = noteService.findAll(pageable);
        return RestApiResponse.success(notes, "Notes retrieved successfully");
    }

    /**
     * Search non-deleted notes by visible text (title + block text) or tag name.
     */
    @GetMapping("/search")
    public ResponseEntity<RestApiResponse<List<NoteDto>>> searchNotes(
            @RequestParam(value = "q", defaultValue = "") String query) {
        List<NoteDto> notes = noteService.searchActive(query);
        return RestApiResponse.success(notes, "Notes retrieved successfully");
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<RestApiResponse<List<NoteDto>>> getNotesByTag(@PathVariable UUID tagId) {
        List<NoteDto> notes = noteService.findActiveByTagId(tagId);
        return RestApiResponse.success(notes, "Notes retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestApiResponse<NoteDto>> updateNote(
            @PathVariable UUID id,
            @Valid @RequestBody CreateNoteRequest request) {
        NoteDto updatedNote = noteService.updateNote(id, request);
        return RestApiResponse.success(updatedNote, "Note updated successfully");
    }

    // --- Lifecycle: pin / archive / reminder ---

    @PatchMapping("/{id}/pin")
    public ResponseEntity<RestApiResponse<NoteDto>> setNotePinned(
            @PathVariable UUID id,
            @Valid @RequestBody SetPinStatusRequest request) {
        NoteDto note = noteService.setPinned(id, request.getPinned());
        return RestApiResponse.success(note, "Note pin status updated successfully");
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<RestApiResponse<NoteDto>> setNoteArchived(
            @PathVariable UUID id,
            @Valid @RequestBody SetArchiveStatusRequest request) {
        NoteDto note = noteService.setArchived(id, request.getArchived());
        return RestApiResponse.success(note, "Note archive status updated successfully");
    }

    @PutMapping("/{id}/reminder")
    public ResponseEntity<RestApiResponse<NoteDto>> setNoteReminder(
            @PathVariable UUID id,
            @Valid @RequestBody SetReminderRequest request) {
        NoteDto note = noteService.setReminder(id, request.getAt(), request.getRepeat());
        return RestApiResponse.success(note, "Note reminder set successfully");
    }

    @DeleteMapping("/{id}/reminder")
    public ResponseEntity<RestApiResponse<NoteDto>> clearNoteReminder(@PathVariable UUID id) {
        NoteDto note = noteService.clearReminder(id);
        return RestApiResponse.success(note, "Note reminder cleared successfully");
    }

    // --- Lifecycle: trash ---

    /**
     * Soft delete a note (move to trash).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteNote(@PathVariable UUID id) {
        noteService.deleteNote(id);
        return RestApiResponse.success("Note moved to trash successfully");
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<RestApiResponse<Void>> deleteNotes(@RequestBody List<UUID> ids) {
        noteService.deleteNotes(ids);
        return RestApiResponse.success("Notes moved to trash successfully");
    }

    /**
     * Trashed notes, optionally filtered by a query (visible text or tag name).
     */
    @GetMapping("/trash")
    public ResponseEntity<RestApiResponse<List<NoteDto>>> getTrashItems(
            @RequestParam(value = "q", defaultValue = "") String query) {
        List<NoteDto> notes = noteService.findAllTrashed(query);
        return RestApiResponse.success(notes, "Trashed notes retrieved successfully");
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<RestApiResponse<NoteDto>> restoreNote(@PathVariable UUID id) {
        NoteDto note = noteService.restoreNote(id);
        return RestApiResponse.success(note, "Note restored successfully");
    }

    /**
     * Permanently delete a single note (bypasses the trash).
     */
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<RestApiResponse<Void>> deleteNotePermanently(@PathVariable UUID id) {
        noteService.deleteNotePermanently(id);
        return RestApiResponse.success("Note permanently deleted successfully");
    }

    /**
     * Permanently delete every trashed note.
     */
    @DeleteMapping("/trash")
    public ResponseEntity<RestApiResponse<Integer>> emptyTrash() {
        int purgedCount = noteService.emptyTrash();
        return RestApiResponse.success(purgedCount, "Trash emptied successfully");
    }

    /**
     * Permanently delete notes that exceeded the trash retention period (30 days).
     * Can also be invoked periodically by a Quartz job calling NoteService.purgeExpiredTrash().
     */
    @PostMapping("/trash/purge")
    public ResponseEntity<RestApiResponse<Integer>> purgeExpiredTrash() {
        int purgedCount = noteService.purgeExpiredTrash();
        return RestApiResponse.success(purgedCount, "Expired trashed notes purged successfully");
    }

    // --- Tags of a note ---

    @GetMapping("/{id}/tags")
    public ResponseEntity<RestApiResponse<List<TagDto>>> getNoteTags(@PathVariable UUID id) {
        noteService.getNoteById(id); // ensure the note exists
        List<TagDto> tags = tagMapper.toDtoList(noteTagService.findTagsByNote(id));
        return RestApiResponse.success(tags, "Note tags retrieved successfully");
    }

    /**
     * Replace the full tag set of a note.
     */
    @PutMapping("/{id}/tags")
    public ResponseEntity<RestApiResponse<NoteDto>> setNoteTags(
            @PathVariable UUID id,
            @Valid @RequestBody SetNoteTagsRequest request) {
        noteTagService.replaceTags(id, request.getTagIds());
        return RestApiResponse.success(noteService.getNoteById(id), "Note tags updated successfully");
    }

    @PostMapping("/{id}/tags/{tagId}")
    public ResponseEntity<RestApiResponse<Void>> assignTagToNote(
            @PathVariable UUID id,
            @PathVariable UUID tagId) {
        noteTagService.assignTagToNote(id, tagId);
        return RestApiResponse.success("Tag assigned to note successfully");
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    public ResponseEntity<RestApiResponse<Void>> removeTagFromNote(
            @PathVariable UUID id,
            @PathVariable UUID tagId) {
        noteTagService.removeTagFromNote(id, tagId);
        return RestApiResponse.success("Tag removed from note successfully");
    }

    // --- Stats ---

    @GetMapping("/count")
    public ResponseEntity<RestApiResponse<Long>> getNoteCount() {
        long count = noteService.count();
        return RestApiResponse.success(count, "Note count retrieved successfully");
    }

    @GetMapping("/count/active")
    public ResponseEntity<RestApiResponse<Long>> getActiveNoteCount() {
        long count = noteService.countActive();
        return RestApiResponse.success(count, "Active note count retrieved successfully");
    }

    @GetMapping("/count/archived")
    public ResponseEntity<RestApiResponse<Long>> getArchivedNoteCount() {
        long count = noteService.countArchived();
        return RestApiResponse.success(count, "Archived note count retrieved successfully");
    }

    @GetMapping("/count/trashed")
    public ResponseEntity<RestApiResponse<Long>> getTrashedNoteCount() {
        long count = noteService.countTrashed();
        return RestApiResponse.success(count, "Trashed note count retrieved successfully");
    }

    @GetMapping("/count/tag/{tagId}")
    public ResponseEntity<RestApiResponse<Long>> getNoteCountByTag(@PathVariable UUID tagId) {
        long count = noteService.countActiveByTagId(tagId);
        return RestApiResponse.success(count, "Note count by tag retrieved successfully");
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<RestApiResponse<Boolean>> checkNoteExists(@PathVariable UUID id) {
        boolean exists = noteService.existsById(id);
        return RestApiResponse.success(exists, "Note existence checked");
    }
}
