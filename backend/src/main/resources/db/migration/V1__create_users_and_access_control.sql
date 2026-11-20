-- User role
CREATE TABLE roles
(
    id          UUID                        NOT NULL,
    created_by  UUID,
    updated_by  UUID,
    created_at  TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    description TEXT,
    name        VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

ALTER TABLE roles
    ADD CONSTRAINT roles_name_key UNIQUE (name);

-- Role permission
CREATE TABLE permissions
(
    id          UUID NOT NULL,
    feature     TEXT NOT NULL,
    action      TEXT NOT NULL,
    description TEXT,
    CONSTRAINT pk_permissions PRIMARY KEY (id)
);


ALTER TABLE permissions
    ADD CONSTRAINT permissions_feature_action_key UNIQUE (feature, action);

-- User - Permission
CREATE TABLE role_permissions
(
    role_id       UUID NOT NULL,
    permission_id UUID NOT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id)
);

ALTER TABLE role_permissions
    ADD CONSTRAINT FK_ROLE_PERMISSIONS_ON_PERMISSION FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE;

ALTER TABLE role_permissions
    ADD CONSTRAINT FK_ROLE_PERMISSIONS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE;

-- User table
CREATE TABLE users
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    name          VARCHAR(255)                NOT NULL,
    username      TEXT                        NOT NULL,
    email         TEXT                        NOT NULL,
    password_hash TEXT                        NOT NULL,
    avatar_url    VARCHAR(255),
    role_id       UUID,
    deleted_at    TIMESTAMP(6) WITH TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT users_email_key UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT users_username_key UNIQUE (username);

CREATE INDEX idx_users_deleted_at ON users (deleted_at);

CREATE INDEX idx_users_role_id ON users (role_id);

CREATE INDEX idx_role_permissions_permission_id ON role_permissions (permission_id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);
