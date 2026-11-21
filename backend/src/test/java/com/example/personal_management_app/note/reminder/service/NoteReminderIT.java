package com.example.personal_management_app.note.reminder.service;

import com.example.personal_management_app.TestcontainersConfiguration;
import com.example.personal_management_app.note.core.dtos.CreateNoteRequest;
import com.example.personal_management_app.note.core.service.NoteService;
import com.example.personal_management_app.user.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {
        "app.reminders.email.enabled=true",
        "app.reminders.email.interval=100ms",
        "app.reminders.email.from=notes@example.org"
})
class NoteReminderIT {
    @Autowired
    NoteService notes;
    @MockitoBean
    UserService users;
    @MockitoBean
    JavaMailSender mail;

    @Test
    void quartzDeliversPersistedReminderToItsCreator() {
        UUID owner = UUID.randomUUID();
        when(users.findActiveEmail(owner)).thenReturn(Optional.of("owner@example.org"));
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(owner, null, List.of()));
        try {
            CreateNoteRequest note = new CreateNoteRequest();
            note.setTitle("Scheduled note");
            note.setContent("{}");
            note.setReminderAt(OffsetDateTime.now().minusMinutes(1));
            notes.createNote(note);
        } finally {
            SecurityContextHolder.clearContext();
        }
        verify(mail, timeout(10000)).send(any(SimpleMailMessage.class));
    }
}
