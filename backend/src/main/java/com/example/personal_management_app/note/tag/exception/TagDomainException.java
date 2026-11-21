package com.example.personal_management_app.note.tag.exception;

import lombok.Getter;
import com.example.personal_management_app.note.tag.enums.TagError;

@Getter
public class TagDomainException extends RuntimeException {
    private final TagError error;

    public TagDomainException(TagError error, String details) {
        super(error.formatMessage(details));
        this.error = error;
    }
}
