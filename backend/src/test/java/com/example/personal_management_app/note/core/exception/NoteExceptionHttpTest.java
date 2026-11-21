package com.example.personal_management_app.note.core.exception;

import com.example.personal_management_app.core.exception.GlobalExceptionHandler;
import com.example.personal_management_app.note.core.controller.NoteController;
import com.example.personal_management_app.note.core.service.NoteService;
import com.example.personal_management_app.note.core.service.NoteTagService;
import com.example.personal_management_app.note.tag.infrastructure.mapstruct.TagMapper;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NoteExceptionHttpTest {
    @Test
    void routesPermissionAndVersionFailuresToTheirHttpStatuses() throws Exception {
        UUID id = UUID.randomUUID();
        NoteService notes = mock(NoteService.class);
        when(notes.getNoteById(id)).thenThrow(new NoteAccessDeniedException("Owner access required"))
                .thenThrow(new StaleNoteVersionException())
                .thenThrow(new OptimisticLockingFailureException("Concurrent save"));
        var mvc = MockMvcBuilders.standaloneSetup(
                        new NoteController(notes, mock(NoteTagService.class), mock(TagMapper.class)))
                .setControllerAdvice(new NoteSpecificExceptionHandler(), new GlobalExceptionHandler(null))
                .build();

        mvc.perform(get("/notes/{id}", id)).andExpect(status().isForbidden());
        mvc.perform(get("/notes/{id}", id)).andExpect(status().isConflict());
        mvc.perform(get("/notes/{id}", id)).andExpect(status().isConflict());
    }
}
