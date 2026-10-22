package com.example.personal_management_app.note.core.infrastructure.persistence.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.example.personal_management_app.core.superbase.BaseFullAuditEntity;
import com.example.personal_management_app.note.core.enums.ReminderRepeat;

import java.time.OffsetDateTime;

/**
 * A single note (title + styled text body).
 * <p>
 * Lifecycle:
 * <ul>
 *     <li>soft delete (trash): {@code deletedAt} is set, row is kept for {@code TRASH_RETENTION_DAYS}</li>
 *     <li>archive: {@code archivedAt} is set (independent of trash)</li>
 *     <li>reminder: flat {@code reminderAt}/{@code reminderRepeat} columns (no scheduling logic here, data only)</li>
 * </ul>
 * Tags are linked through the {@link NoteTag} join entity (tags are independent, shareable entities).
 * {@code createdBy} (from the audit base) acts as the note owner for future sharing/user scoping.
 * </p>
 * <p>
 * Content model: {@code content} holds the frontend block-document (jsonb), while
 * {@code searchText} holds a derived plain-text projection used by search.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "notes", indexes = {
        @Index(name = "idx_notes_deleted_at", columnList = "deleted_at"),
        @Index(name = "idx_notes_archived_at", columnList = "archived_at"),
        @Index(name = "idx_notes_pinned", columnList = "pinned"),
        @Index(name = "idx_notes_reminder_at", columnList = "reminder_at")
})
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", nullable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at", nullable = false)),
        @AttributeOverride(name = "createdBy", column = @Column(name = "created_by")),
        @AttributeOverride(name = "updatedBy", column = @Column(name = "updated_by"))
})
public class Note extends BaseFullAuditEntity {

    @NotNull
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    /**
     * Block-document body (the frontend's NoteContentDocument JSON: version, titleSpans, blocks).
     * Stored as a jsonb document - the backend treats it as verbatim store-and-return and never
     * interprets it on read, so the frontend can evolve the block schema freely.
     */
    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content", nullable = false, columnDefinition = "jsonb")
    private String content;

    /**
     * Plain-text search projection (title + all block text, in document order).
     * Maintained by the service on every save so search never matches JSON structure tokens.
     */
    @NotNull
    @Column(name = "search_text", nullable = false, length = Integer.MAX_VALUE)
    private String searchText;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fontSize", column = @Column(name = "title_font_size", nullable = false)),
            @AttributeOverride(name = "bold", column = @Column(name = "title_bold", nullable = false)),
            @AttributeOverride(name = "italic", column = @Column(name = "title_italic", nullable = false)),
            @AttributeOverride(name = "underline", column = @Column(name = "title_underline", nullable = false))
    })
    private NoteTextStyle titleStyle = new NoteTextStyle();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fontSize", column = @Column(name = "content_font_size", nullable = false)),
            @AttributeOverride(name = "bold", column = @Column(name = "content_bold", nullable = false)),
            @AttributeOverride(name = "italic", column = @Column(name = "content_italic", nullable = false)),
            @AttributeOverride(name = "underline", column = @Column(name = "content_underline", nullable = false))
    })
    private NoteTextStyle contentStyle = new NoteTextStyle();

    @Column(name = "background_color", length = 32)
    private String backgroundColor;

    @NotNull
    @Column(name = "pinned", nullable = false)
    private boolean pinned = false;

    @Column(name = "archived_at")
    private OffsetDateTime archivedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "reminder_at")
    private OffsetDateTime reminderAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_repeat", length = 16)
    private ReminderRepeat reminderRepeat;
}
