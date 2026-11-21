package com.example.personal_management_app.note.core.infrastructure.persistence.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class NoteTagId implements Serializable {
    @Serial
    private static final long serialVersionUID = 4822943825629183357L;

    @NotNull
    @Column(name = "note_id", nullable = false)
    private UUID noteId;

    @NotNull
    @Column(name = "tag_id", nullable = false)
    private UUID tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        NoteTagId entity = (NoteTagId) o;
        return Objects.equals(this.noteId, entity.noteId) &&
                Objects.equals(this.tagId, entity.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteId, tagId);
    }
}
