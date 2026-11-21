CREATE TABLE notes
(
    id                UUID                        NOT NULL,
    created_by        UUID,
    updated_by        UUID,
    created_at        TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at        TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    title             VARCHAR(255)                NOT NULL,
    content           JSONB                       NOT NULL,
    search_text       TEXT                        NOT NULL,
    background_color  VARCHAR(32),
    pinned            BOOLEAN                     NOT NULL,
    archived_at       TIMESTAMP(6) WITH TIME ZONE,
    deleted_at        TIMESTAMP(6) WITH TIME ZONE,
    reminder_at       TIMESTAMP(6) WITH TIME ZONE,
    reminder_repeat   VARCHAR(16),
    title_font_size   REAL                        NOT NULL,
    title_bold        BOOLEAN                     NOT NULL,
    title_italic      BOOLEAN                     NOT NULL,
    title_underline   BOOLEAN                     NOT NULL,
    content_font_size REAL                        NOT NULL,
    content_bold      BOOLEAN                     NOT NULL,
    content_italic    BOOLEAN                     NOT NULL,
    content_underline BOOLEAN                     NOT NULL,
    CONSTRAINT pk_notes PRIMARY KEY (id)
);

CREATE INDEX idx_notes_archived_at ON notes (archived_at);

CREATE INDEX idx_notes_deleted_at ON notes (deleted_at);

CREATE INDEX idx_notes_pinned ON notes (pinned);

CREATE INDEX idx_notes_reminder_at ON notes (reminder_at);

-- Tag of note
CREATE TABLE tags
(
    owner_id   UUID                        NOT NULL,
    id         UUID                        NOT NULL,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    name       VARCHAR(100)                NOT NULL,
    color      VARCHAR(32),
    CONSTRAINT pk_tags PRIMARY KEY (id)
);

ALTER TABLE tags
    ADD CONSTRAINT uq_tags_owner_id_name UNIQUE (owner_id, name);

ALTER TABLE tags
    ADD CONSTRAINT fk_tags_users FOREIGN KEY (owner_id) REFERENCES users (id);

-- Tag to note
CREATE TABLE note_tags
(
    note_id UUID NOT NULL,
    tag_id  UUID NOT NULL,
    CONSTRAINT pk_note_tags PRIMARY KEY (note_id, tag_id)
);

ALTER TABLE note_tags
    ADD CONSTRAINT FK_NOTE_TAGS_ON_NOTE FOREIGN KEY (note_id) REFERENCES notes (id) ON DELETE CASCADE;

ALTER TABLE note_tags
    ADD CONSTRAINT FK_NOTE_TAGS_ON_TAG FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE;
CREATE INDEX idx_note_tags_tag_id ON note_tags (tag_id);
