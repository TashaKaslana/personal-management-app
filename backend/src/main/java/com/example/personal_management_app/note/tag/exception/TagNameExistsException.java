package com.example.personal_management_app.note.tag.exception;

import com.example.personal_management_app.note.tag.enums.TagError;

/**
 * Exception thrown when a tag name already exists
 */
public class TagNameExistsException extends TagDomainException {
    public TagNameExistsException(String name) {
        super(TagError.TAG_NAME_EXISTS, name);
    }
}
