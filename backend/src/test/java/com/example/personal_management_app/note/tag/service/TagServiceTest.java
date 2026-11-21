package com.example.personal_management_app.note.tag.service;

import com.example.personal_management_app.note.core.service.NoteTagService;
import com.example.personal_management_app.note.tag.dtos.CreateTagRequest;
import com.example.personal_management_app.note.tag.exception.TagNameExistsException;
import com.example.personal_management_app.note.tag.exception.TagNotFoundException;
import com.example.personal_management_app.note.tag.infrastructure.mapstruct.TagMapper;
import com.example.personal_management_app.note.tag.infrastructure.persistence.entities.Tag;
import com.example.personal_management_app.note.tag.infrastructure.persistence.repositories.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.personal_management_app.core.security.CurrentUserProvider;
import com.example.personal_management_app.note.tag.exception.TagOwnerUnavailableException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
    @Mock private CurrentUserProvider currentUserProvider;
    @Mock private TagRepository tagRepository;
    @Mock private TagMapper tagMapper;
    @Mock private NoteTagService noteTagService;
    @InjectMocks private TagService tagService;

    @Test
    void createTag_shouldAssignCurrentOwner_whenNameIsAvailableForOwner() {
        UUID ownerId = UUID.randomUUID();
        CreateTagRequest request = new CreateTagRequest();
        request.setName("Work");
        Tag tag = new Tag();
        when(currentUserProvider.getCurrentUserId()).thenReturn(Optional.of(ownerId));
        when(tagMapper.toEntity(request)).thenReturn(tag);
        when(tagRepository.save(tag)).thenReturn(tag);

        tagService.createTag(request);

        assertThat(tag.getOwnerId()).isEqualTo(ownerId);
        verify(tagRepository).existsByNameAndOwnerId("Work", ownerId);
        verify(tagRepository).save(tag);
    }

    @Test
    void createTag_shouldRejectDuplicate_whenOwnerAlreadyHasName() {
        UUID ownerId = UUID.randomUUID();
        CreateTagRequest request = new CreateTagRequest();
        request.setName("Work");
        when(currentUserProvider.getCurrentUserId()).thenReturn(Optional.of(ownerId));
        when(tagRepository.existsByNameAndOwnerId("Work", ownerId)).thenReturn(true);

        assertThatThrownBy(() -> tagService.createTag(request)).isInstanceOf(TagNameExistsException.class);
        verify(tagRepository, never()).save(any());
    }

    @Test
    void deleteTag_shouldRejectAccess_whenTagDoesNotBelongToOwner() {
        UUID ownerId = UUID.randomUUID();
        UUID tagId = UUID.randomUUID();
        when(currentUserProvider.getCurrentUserId()).thenReturn(Optional.of(ownerId));
        when(tagRepository.findByIdAndOwnerId(tagId, ownerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.deleteTag(tagId)).isInstanceOf(TagNotFoundException.class);
        verifyNoInteractions(noteTagService);
        verify(tagRepository, never()).delete(any());
    }

    @Test
    void findByName_shouldRejectAccess_whenCurrentOwnerIsMissing() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.findByName("Work"))
            .isInstanceOf(TagOwnerUnavailableException.class);
        verifyNoInteractions(tagRepository);
    }
}
