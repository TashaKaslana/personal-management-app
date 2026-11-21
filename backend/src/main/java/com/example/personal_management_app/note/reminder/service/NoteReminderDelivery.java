package com.example.personal_management_app.note.reminder.service;

import com.example.personal_management_app.note.reminder.enums.ReminderRepeat;
import com.example.personal_management_app.note.core.dtos.NoteDto;
import com.example.personal_management_app.note.core.service.NoteService;
import com.example.personal_management_app.user.core.service.UserService;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.reminders.email.enabled", havingValue = "true")
public class NoteReminderDelivery {
    private final NoteService notes;
    private final UserService users;
    private final JavaMailSender mail;

    @Value("${app.reminders.email.from}")
    private String from;

    @Transactional
    public void sendIfDue(UUID id, OffsetDateTime now) {
        NoteDto note = notes.lockDueReminder(id, now).orElse(null);
        if (note == null || note.getCreatedBy() == null) {
            return;
        }
        users.findActiveEmail(note.getCreatedBy()).ifPresent(address -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(address);
            message.setSubject("Note reminder");
            message.setText("You scheduled a reminder for this note:\n\n" + note.getTitle());
            mail.send(message);
        });

        OffsetDateTime next = nextReminder(note.getReminderAt(), note.getReminderRepeat(), now);
        notes.advanceLockedReminder(id, next);
    }

    static OffsetDateTime nextReminder(OffsetDateTime due, ReminderRepeat repeat, OffsetDateTime now) {
        if (repeat == null || repeat == ReminderRepeat.ONCE) {
            return null;
        }
        OffsetDateTime next = due;
        do {
            next = switch (repeat) {
                case DAILY -> next.plusDays(1);
                case WEEKLY -> next.plusWeeks(1);
                case MONTHLY -> next.plusMonths(1);
                default -> throw new IllegalArgumentException("Unexpected repeat interval");
            };
        } while (!next.isAfter(now));
        return next;
    }
}
