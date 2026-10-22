package com.example.personal_management_app.note.core.infrastructure.persistence.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.example.personal_management_app.note.core.infrastructure.persistence.key.NoteTagId;
import com.example.personal_management_app.note.tag.infrastructure.persistence.entities.Tag;

@Getter
@Setter
@Entity
@Table(name = "note_tags")
public class NoteTag {
    @EmbeddedId
    private NoteTagId id;

    @MapsId("noteId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}
