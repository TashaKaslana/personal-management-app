package com.example.personal_management_app.note.reminder.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;

class NoteReminderMailConfigurationTest {
    @Test
    void applicationPropertiesConfigureSmtpSender() {
        new ApplicationContextRunner()
                .withInitializer(new ConfigDataApplicationContextInitializer())
                .withConfiguration(AutoConfigurations.of(MailSenderAutoConfiguration.class))
                .run(context -> assertThat(context).hasSingleBean(JavaMailSender.class));
    }
}
