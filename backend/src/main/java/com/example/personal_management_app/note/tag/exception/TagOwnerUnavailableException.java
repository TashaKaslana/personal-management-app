package com.example.personal_management_app.note.tag.exception;

import com.example.personal_management_app.note.tag.enums.TagError;

/** Thrown when the owner provider cannot resolve an owner for a tag operation. */
public class TagOwnerUnavailableException extends TagDomainException {
    public TagOwnerUnavailableException() {
        super(TagError.TAG_OWNER_UNAVAILABLE, "No current owner could be resolved");
    }
}
