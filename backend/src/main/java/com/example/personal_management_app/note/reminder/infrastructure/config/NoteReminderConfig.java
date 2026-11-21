package com.example.personal_management_app.note.reminder.infrastructure.config;

import com.example.personal_management_app.core.services.SchedulerService;
import java.time.Duration;

import com.example.personal_management_app.note.reminder.scheduling.NoteReminderJob;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.reminders.email.enabled", havingValue = "true")
public class NoteReminderConfig {
    @Bean
    JobDetail noteReminderJob() {
        return SchedulerService.recurringJob("notes", "email-reminders", NoteReminderJob.class);
    }

    @Bean
    Trigger noteReminderTrigger(@Qualifier("noteReminderJob") JobDetail job,
            @Value("${app.reminders.email.interval:30s}") Duration interval) {
        return SchedulerService.recurringTrigger(job, interval);
    }
}
