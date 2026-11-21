package com.example.personal_management_app.note.reminder.scheduling;

import com.example.personal_management_app.note.core.service.NoteService;
import com.example.personal_management_app.note.reminder.service.NoteReminderDelivery;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class NoteReminderJob implements Job {
    private final NoteService notes;
    private final NoteReminderDelivery delivery;

    @Override
    public void execute(JobExecutionContext context) {
        OffsetDateTime now = OffsetDateTime.now();
        for (UUID id : notes.findDueReminderIds(now)) {
            try {
                delivery.sendIfDue(id, now);
            } catch (RuntimeException ex) {
                log.warn("Reminder delivery for note {} failed and will be retried ({})",
                        id, ex.getClass().getSimpleName());
            }
        }
    }
}
