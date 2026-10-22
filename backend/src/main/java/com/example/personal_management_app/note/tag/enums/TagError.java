package com.example.personal_management_app.note.tag.enums;

import lombok.Getter;

/**
 * Enumeration of tag-specific error messages
 */
@Getter
public enum TagError {
    TAG_NOT_FOUND("tag.not_found", "Tag not found"),
    TAG_LIST_NOT_FOUND("tag.list_not_found", "One or more tags not found"),
    TAG_NAME_EXISTS("tag.name_exists", "Tag name already exists");

    private final String code;
    private final String message;

    TagError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Format error message with additional details
     * @param details Additional details to append to the message
     * @return Formatted error message
     */
    public String formatMessage(String details) {
        return this.message + ": " + details;
    }
}
