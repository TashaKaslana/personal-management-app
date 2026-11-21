package com.example.personal_management_app.note.reminder.service;

import com.example.personal_management_app.note.reminder.enums.ReminderRepeat;
import com.example.personal_management_app.note.core.dtos.NoteDto;
import com.example.personal_management_app.note.core.service.NoteService;
import com.example.personal_management_app.user.core.service.UserService;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteReminderDeliveryTest {
    @Mock NoteService notes;
    @Mock UserService users;
    @Mock JavaMailSender mail;
    @InjectMocks NoteReminderDelivery delivery;

    private final UUID id = UUID.randomUUID();
    private final OffsetDateTime now = OffsetDateTime.parse("2026-06-20T10:00:00Z");

    private NoteDto dueNote() {
        NoteDto note = mock(NoteDto.class);
        UUID owner = UUID.randomUUID();
        when(notes.lockDueReminder(id, now)).thenReturn(Optional.of(note));
        when(note.getCreatedBy()).thenReturn(owner);
        when(users.findActiveEmail(owner)).thenReturn(Optional.of("owner@example.org"));
        return note;
    }

    @Test
    void sendsOnceToOwnerAndClearsReminder() {
        NoteDto note = dueNote();
        when(note.getTitle()).thenReturn("Meeting");
        delivery.sendIfDue(id, now);

        var message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mail).send(message.capture());
        assertThat(message.getValue().getTo()).containsExactly("owner@example.org");
        assertThat(message.getValue().getText()).contains("Meeting");
        verify(notes).advanceLockedReminder(id, null);
    }

    @Test
    void leavesReminderDueOnMailFailure() {
        dueNote();
        doThrow(new MailSendException("Unavailable")).when(mail).send(any(SimpleMailMessage.class));
        assertThatThrownBy(() -> delivery.sendIfDue(id, now)).isInstanceOf(MailSendException.class);
        verify(notes, never()).advanceLockedReminder(any(), any());
    }

    @Test
    void skipsCancelledOrTrashedReminder() {
        when(notes.lockDueReminder(id, now)).thenReturn(Optional.empty());
        delivery.sendIfDue(id, now);
        verifyNoInteractions(users, mail);
    }

    @Test
    void skipsMissedOccurrencesAfterDowntime() {
        assertThat(NoteReminderDelivery.nextReminder(now.minusDays(8), ReminderRepeat.WEEKLY, now))
                .isEqualTo(now.plusDays(6));
        assertThat(NoteReminderDelivery.nextReminder(now.minusDays(4), ReminderRepeat.DAILY, now))
                .isEqualTo(now.plusDays(1));
        assertThat(NoteReminderDelivery.nextReminder(now.minusMonths(2), ReminderRepeat.MONTHLY, now))
                .isEqualTo(now.plusMonths(1));
    }
}
