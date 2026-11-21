package com.example.personal_management_app.note.collaboration.service;

import com.example.personal_management_app.note.core.service.NoteService;

import com.example.personal_management_app.TestcontainersConfiguration;
import com.example.personal_management_app.note.core.dtos.CreateNoteRequest;
import com.example.personal_management_app.note.core.dtos.UpdateNoteRequest;
import com.example.personal_management_app.note.core.dtos.NoteTextStyleDto;
import com.example.personal_management_app.note.collaboration.enums.CollaboratorRole;
import com.example.personal_management_app.note.core.exception.NoteNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import com.example.personal_management_app.note.core.exception.StaleNoteVersionException;
import org.springframework.jdbc.core.JdbcTemplate;
import com.example.personal_management_app.note.core.exception.NoteAccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class NoteCollaborationIT {
    @Autowired NoteService notes;
    @Autowired NoteCollaborationService sharing;
    @Autowired JdbcTemplate jdbc;

    @AfterEach
    void clearIdentity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void sharingControlsDiscoveryEditingAndConflictRecovery() {
        UUID owner = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        jdbc.update("""
                insert into users (id, created_at, updated_at, name, username, email, password_hash)
                values (?, now(), now(), 'Member', ?, ?, 'unused')
                """, member, member.toString(), member + "@example.org");
        signIn(owner);
        CreateNoteRequest create = new CreateNoteRequest();
        create.setTitle("Draft");
        create.setPinned(true);
        NoteTextStyleDto titleStyle = new NoteTextStyleDto();
        titleStyle.setBold(true);
        create.setTitleStyle(titleStyle);
        create.setContent("{}");
        UUID id = notes.createNote(create).getId();

        signIn(member);
        assertThat(notes.findAllActive()).isEmpty();
        assertThatThrownBy(() -> notes.getNoteById(id)).isInstanceOf(NoteNotFoundException.class);

        signIn(owner);
        sharing.setCollaborator(id, member, CollaboratorRole.VIEWER);
        assertThat(sharing.collaborators(id)).containsEntry(member, CollaboratorRole.VIEWER);
        signIn(member);
        assertThat(notes.findAllActive()).hasSize(1);
        UpdateNoteRequest edit = new UpdateNoteRequest();
        edit.setTitle("Revised");
        edit.setContent("{}");
        edit.setVersion(notes.getNoteById(id).getVersion());
        assertThatThrownBy(() -> notes.updateNote(id, edit)).isInstanceOf(NoteAccessDeniedException.class);

        signIn(owner);
        sharing.setCollaborator(id, member, CollaboratorRole.EDITOR);
        signIn(member);
        assertThatThrownBy(() -> notes.deleteNote(id)).isInstanceOf(NoteAccessDeniedException.class);
        edit.setVersion(notes.getNoteById(id).getVersion());
        var updated = notes.updateNote(id, edit);
        assertThat(updated.getVersion()).isGreaterThan(edit.getVersion());
        assertThat(updated.isPinned()).isTrue();
        assertThat(updated.getTitleStyle().isBold()).isFalse();
        assertThat(updated.getTitleStyle().getFontSize()).isEqualTo(16f);
        signIn(owner);
        assertThatThrownBy(() -> notes.updateNote(id, edit)).isInstanceOf(StaleNoteVersionException.class);
        assertThat(notes.getNoteById(id).getTitle()).isEqualTo("Revised");
        sharing.removeCollaborator(id, member);
        signIn(member);
        assertThat(notes.count()).isZero();
    }

    private void signIn(UUID user) {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(user, null, List.of()));
    }
}
