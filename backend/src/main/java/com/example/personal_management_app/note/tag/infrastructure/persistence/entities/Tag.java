package com.example.personal_management_app.note.tag.infrastructure.persistence.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.example.personal_management_app.core.superbase.BaseFullAuditEntity;

/**
 * An independent tag that can be attached to notes (and later to other entities).
 * Renaming a tag automatically updates every linked note (no per-note rewrite needed).
 */
@Getter
@Setter
@Entity
@Table(name = "tags", uniqueConstraints = {
        @UniqueConstraint(name = "tags_name_key", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", nullable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at", nullable = false)),
        @AttributeOverride(name = "createdBy", column = @Column(name = "created_by")),
        @AttributeOverride(name = "updatedBy", column = @Column(name = "updated_by"))
})
public class Tag extends BaseFullAuditEntity {

    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "color", length = 32)
    private String color;
}
