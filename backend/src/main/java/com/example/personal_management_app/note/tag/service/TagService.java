package com.example.personal_management_app.note.tag.service;

import lombok.RequiredArgsConstructor;
import com.example.personal_management_app.note.core.infrastructure.persistence.entities.Note;
import com.example.personal_management_app.note.core.service.NoteTagService;
import com.example.personal_management_app.note.tag.dtos.CreateTagRequest;
import com.example.personal_management_app.note.tag.dtos.TagDto;
import com.example.personal_management_app.note.tag.exception.TagNameExistsException;
import com.example.personal_management_app.note.tag.exception.TagNotFoundException;
import com.example.personal_management_app.note.tag.infrastructure.mapstruct.TagMapper;
import com.example.personal_management_app.note.tag.infrastructure.persistence.entities.Tag;
import com.example.personal_management_app.note.tag.infrastructure.persistence.repositories.TagRepository;
import org.springframework.data.domain.Page;
import com.example.personal_management_app.core.security.CurrentUserProvider;
import com.example.personal_management_app.note.tag.exception.TagOwnerUnavailableException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Tag CRUD + note-link operations. Because a tag is an independent entity,
 * renaming it automatically updates every linked note (no per-note rewrite needed).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final CurrentUserProvider currentUserProvider;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final NoteTagService noteTagService;

    /**
     * Create a new tag using DTO
     */
    @Transactional
    public TagDto createTag(CreateTagRequest request) {
        if (tagRepository.existsByNameAndOwnerId(request.getName(), currentOwnerId())) {
            throw new TagNameExistsException(request.getName());
        }

        Tag tag = tagMapper.toEntity(request);
        tag.setOwnerId(currentOwnerId());
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toDto(savedTag);
    }

    /**
     * Create multiple tags in bulk
     */
    @Transactional
    public List<TagDto> createTags(List<CreateTagRequest> requests) {
        return requests.stream()
            .map(this::createTag)
            .toList();
    }

    /**
     * Find a tag by ID
     */
    public TagDto findById(UUID id) {
        return tagRepository.findByIdAndOwnerId(id, currentOwnerId())
            .map(tagMapper::toDto)
            .orElseThrow(() -> new TagNotFoundException(id.toString()));
    }

    /**
     * Find a tag by name
     */
    public TagDto findByName(String name) {
        return tagRepository.findByNameAndOwnerId(name, currentOwnerId())
            .map(tagMapper::toDto)
            .orElseThrow(() -> new TagNotFoundException("name", name));
    }

    /**
     * Find all tags
     */
    public List<TagDto> findAll() {
        List<Tag> tags = tagRepository.findAllByOwnerId(currentOwnerId());
        return tagMapper.toDtoList(tags);
    }

    /**
     * Find tags with pagination
     */
    public Page<TagDto> findAll(Pageable pageable) {
        return tagRepository.findAllByOwnerId(currentOwnerId(), pageable)
            .map(tagMapper::toDto);
    }

    /**
     * Update a tag (rename / recolor). Renaming is reflected on every linked note.
     */
    @Transactional
    public TagDto updateTag(UUID id, CreateTagRequest request) {
        Tag tag = tagRepository.findByIdAndOwnerId(id, currentOwnerId())
            .orElseThrow(() -> new TagNotFoundException(id.toString()));

        if (tagRepository.existsByNameAndOwnerIdAndIdNot(request.getName(), currentOwnerId(), id)) {
            throw new TagNameExistsException(request.getName());
        }

        tagMapper.updateEntity(request, tag);
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toDto(savedTag);
    }

    /**
     * Delete a tag (also removes all its note links)
     */
    @Transactional
    public void deleteTag(UUID id) {
        Tag tag = tagRepository.findByIdAndOwnerId(id, currentOwnerId())
            .orElseThrow(() -> new TagNotFoundException(id.toString()));

        noteTagService.removeAllNoteLinksFromTag(id);
        tagRepository.delete(tag);
    }

    /**
     * Remove a tag from every note but keep the tag itself.
     * Returns the number of removed note links.
     */
    @Transactional
    public int clearTag(UUID id) {
        if (!tagRepository.existsByIdAndOwnerId(id, currentOwnerId())) {
            throw new TagNotFoundException(id.toString());
        }

        return noteTagService.removeAllNoteLinksFromTag(id);
    }

    /**
     * Find all non-deleted notes linked to a tag
     */
    public List<Note> findNotesByTag(UUID tagId) {
        if (!tagRepository.existsByIdAndOwnerId(tagId, currentOwnerId())) {
            throw new TagNotFoundException(tagId.toString());
        }

        return noteTagService.findActiveNotesByTag(tagId);
    }

    /**
     * Count non-deleted notes linked to a tag
     */
    public long countNotesByTag(UUID tagId) {
        if (!tagRepository.existsByIdAndOwnerId(tagId, currentOwnerId())) {
            throw new TagNotFoundException(tagId.toString());
        }

        return noteTagService.countActiveNotesByTag(tagId);
    }

    /**
     * Count all note links of a tag (including trashed notes)
     */
    public long countNoteLinksByTag(UUID tagId) {
        if (!tagRepository.existsByIdAndOwnerId(tagId, currentOwnerId())) {
            throw new TagNotFoundException(tagId.toString());
        }

        return noteTagService.countNoteLinksByTag(tagId);
    }

    /**
     * Check if a tag exists
     */
    public boolean existsById(UUID id) {
        return tagRepository.existsByIdAndOwnerId(id, currentOwnerId());
    }

    /**
     * Check if a tag exists by name
     */
    public boolean existsByName(String name) {
        return tagRepository.existsByNameAndOwnerId(name, currentOwnerId());
    }

    /**
     * Count all tags
     */
    public long count() {
        return tagRepository.countByOwnerId(currentOwnerId());
    }

    private UUID currentOwnerId() {
        return currentUserProvider.getCurrentUserId()
            .orElseThrow(TagOwnerUnavailableException::new);
    }

    // Internal method for getting entity (used by other services)
    public Optional<Tag> findEntityByName(String name) {
        return tagRepository.findByNameAndOwnerId(name, currentOwnerId());
    }
}
