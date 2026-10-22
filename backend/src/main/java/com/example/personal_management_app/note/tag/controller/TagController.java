package com.example.personal_management_app.note.tag.controller;

import lombok.RequiredArgsConstructor;
import com.example.personal_management_app.core.responses.RestApiResponse;
import com.example.personal_management_app.note.core.dtos.NoteDto;
import com.example.personal_management_app.note.core.service.NoteService;
import com.example.personal_management_app.note.tag.dtos.CreateTagRequest;
import com.example.personal_management_app.note.tag.dtos.TagDto;
import com.example.personal_management_app.note.tag.service.TagService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<RestApiResponse<TagDto>> createTag(@Valid @RequestBody CreateTagRequest request) {
        TagDto createdTag = tagService.createTag(request);
        return RestApiResponse.created(createdTag, "Tag created successfully");
    }

    @PostMapping("/bulk")
    public ResponseEntity<RestApiResponse<List<TagDto>>> createTags(@Valid @RequestBody List<CreateTagRequest> requests) {
        List<TagDto> createdTags = tagService.createTags(requests);
        return RestApiResponse.created(createdTags, "Tags created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<TagDto>> getTagById(@PathVariable UUID id) {
        TagDto tag = tagService.findById(id);
        return RestApiResponse.success(tag, "Tag retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<TagDto>>> getAllTags() {
        List<TagDto> tags = tagService.findAll();
        return RestApiResponse.success(tags, "Tags retrieved successfully");
    }

    @GetMapping("/paginated")
    public ResponseEntity<RestApiResponse<List<TagDto>>> getAllTags(Pageable pageable) {
        Page<TagDto> tags = tagService.findAll(pageable);
        return RestApiResponse.success(tags, "Tags retrieved successfully");
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<RestApiResponse<TagDto>> getTagByName(@PathVariable String name) {
        TagDto tag = tagService.findByName(name);
        return RestApiResponse.success(tag, "Tag retrieved successfully");
    }

    /**
     * Update a tag (rename / recolor). Renaming is automatically reflected on every linked note.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestApiResponse<TagDto>> updateTag(
            @PathVariable UUID id,
            @Valid @RequestBody CreateTagRequest request) {
        TagDto updatedTag = tagService.updateTag(id, request);
        return RestApiResponse.success(updatedTag, "Tag updated successfully");
    }

    /**
     * Delete a tag (also removes all its note links).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return RestApiResponse.success("Tag deleted successfully");
    }

    /**
     * Remove a tag from every note but keep the tag itself.
     */
    @DeleteMapping("/{id}/links")
    public ResponseEntity<RestApiResponse<Integer>> clearTag(@PathVariable UUID id) {
        int removedLinks = tagService.clearTag(id);
        return RestApiResponse.success(removedLinks, "Tag removed from all notes successfully");
    }

    /**
     * All non-deleted notes linked to a tag.
     */
    @GetMapping("/{id}/notes")
    public ResponseEntity<RestApiResponse<List<NoteDto>>> getNotesByTag(@PathVariable UUID id) {
        List<NoteDto> notes = tagService.findNotesByTag(id).stream()
            .map(note -> noteService.getNoteById(note.getId()))
            .toList();
        return RestApiResponse.success(notes, "Notes by tag retrieved successfully");
    }

    /**
     * Count non-deleted notes linked to a tag.
     */
    @GetMapping("/{id}/note-count")
    public ResponseEntity<RestApiResponse<Long>> getNoteCountByTag(@PathVariable UUID id) {
        long count = tagService.countNotesByTag(id);
        return RestApiResponse.success(count, "Note count by tag retrieved successfully");
    }

    @GetMapping("/count")
    public ResponseEntity<RestApiResponse<Long>> getTagCount() {
        long count = tagService.count();
        return RestApiResponse.success(count, "Tag count retrieved successfully");
    }

    @GetMapping("/check/name/{name}")
    public ResponseEntity<RestApiResponse<Boolean>> checkTagExistsByName(@PathVariable String name) {
        boolean exists = tagService.existsByName(name);
        return RestApiResponse.success(exists, "Tag existence checked");
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<RestApiResponse<Boolean>> checkTagExists(@PathVariable UUID id) {
        boolean exists = tagService.existsById(id);
        return RestApiResponse.success(exists, "Tag existence checked");
    }
}
