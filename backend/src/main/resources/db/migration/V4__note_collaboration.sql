ALTER TABLE notes ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

CREATE TABLE note_collaborators (
    note_id UUID NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(16) NOT NULL CHECK (role IN ('EDITOR', 'VIEWER')),
    CONSTRAINT pk_note_collaborators PRIMARY KEY (note_id, user_id)
);

CREATE INDEX idx_note_collaborators_user_id ON note_collaborators(user_id);
