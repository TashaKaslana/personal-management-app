package com.example.personal_management_app.note.tag.exception;

import com.example.personal_management_app.note.tag.enums.TagError;

/**
 * Exception thrown when a tag is not found
 */
public class TagNotFoundException extends TagDomainException {
    public TagNotFoundException(String tagId) {
        super(TagError.TAG_NOT_FOUND, tagId);
    }

    public TagNotFoundException(String property, String value) {
        super(TagError.TAG_NOT_FOUND, property + ": " + value);
    }

    public TagNotFoundException(TagError error, String details) {
        super(error, details);
    }
}
