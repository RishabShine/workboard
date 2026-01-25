-- V1__init.sql
-- Core schema for Workboard (PostgreSQL)

-- USERS
CREATE TABLE users (
                       id                BIGSERIAL PRIMARY KEY,
                       username          VARCHAR(255) NOT NULL UNIQUE,
                       email             VARCHAR(255) UNIQUE,
                       password_hash     VARCHAR(255),
                       profile_image_key VARCHAR(1024),
                       bio               TEXT,
                       created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- PROJECTS
CREATE TABLE projects (
                          id         BIGSERIAL PRIMARY KEY,
                          name       VARCHAR(255) NOT NULL,
                          created_by BIGINT NOT NULL REFERENCES users(id),
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ROLES (scoped to a project)
CREATE TABLE roles (
                       id         BIGSERIAL PRIMARY KEY,
                       name       VARCHAR(100) NOT NULL,
                       project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                       created_by BIGINT REFERENCES users(id),
                       created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                       CONSTRAINT uq_roles_project_name UNIQUE (project_id, name)
);

-- MEMBERS (membership + role per project)
CREATE TABLE members (
                         user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                         role_id    BIGINT NOT NULL REFERENCES roles(id),
                         joined_on  TIMESTAMPTZ NOT NULL DEFAULT now(),
                         PRIMARY KEY (user_id, project_id)
);

-- MILESTONES
CREATE TABLE milestones (
                            id         BIGSERIAL PRIMARY KEY,
                            name       VARCHAR(255) NOT NULL,
                            project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                            created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                            CONSTRAINT uq_milestones_project_name UNIQUE (project_id, name)
);

-- TAGS
CREATE TABLE tags (
                      id         BIGSERIAL PRIMARY KEY,
                      name       VARCHAR(255) NOT NULL,
                      color      VARCHAR(50),
                      project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                      created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                      CONSTRAINT uq_tags_project_name UNIQUE (project_id, name)
);

-- TICKETS
CREATE TABLE tickets (
                         id           BIGSERIAL PRIMARY KEY,
                         project_id   BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                         title        VARCHAR(255) NOT NULL,
                         body         TEXT,
                         status       VARCHAR(50),
                         created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
                         created_by   BIGINT NOT NULL REFERENCES users(id),
                         assigned_to  BIGINT REFERENCES users(id),
                         milestone_id BIGINT REFERENCES milestones(id)
);

-- TICKET_TAGS (many-to-many)
CREATE TABLE ticket_tags (
                             ticket_id BIGINT NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
                             tag_id    BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
                             PRIMARY KEY (ticket_id, tag_id)
);

-- COMMENTS
CREATE TABLE comments (
                          id         BIGSERIAL PRIMARY KEY,
                          ticket_id  BIGINT NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
                          user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          body       TEXT NOT NULL,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                          reply_to   BIGINT REFERENCES comments(id) ON DELETE SET NULL
);

-- TICKET_SUBSCRIPTIONS (many-to-many)
CREATE TABLE ticket_subscriptions (
                                      ticket_id  BIGINT NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
                                      user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                      created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                      PRIMARY KEY (ticket_id, user_id)
);

-- PROJECT INVITES
-- status values you can use: PENDING, ACCEPTED, DECLINED, EXPIRED
CREATE TABLE project_invites (
                                 id           BIGSERIAL PRIMARY KEY,
                                 project_id   BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                                 recipient_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 invited_by   BIGINT NOT NULL REFERENCES users(id),
                                 status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                 created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Ensure only one *pending* invite exists per (project, recipient)
CREATE UNIQUE INDEX uq_project_invites_pending
    ON project_invites (project_id, recipient_id)
    WHERE status = 'PENDING';

-- Helpful indexes for common query patterns
CREATE INDEX idx_members_project_id ON members(project_id);
CREATE INDEX idx_roles_project_id ON roles(project_id);
CREATE INDEX idx_milestones_project_id ON milestones(project_id);
CREATE INDEX idx_tags_project_id ON tags(project_id);

CREATE INDEX idx_tickets_project_id ON tickets(project_id);
CREATE INDEX idx_tickets_assigned_to ON tickets(assigned_to);
CREATE INDEX idx_tickets_created_by ON tickets(created_by);

CREATE INDEX idx_comments_ticket_id ON comments(ticket_id);

CREATE INDEX idx_invites_recipient_id ON project_invites(recipient_id);
CREATE INDEX idx_invites_project_id ON project_invites(project_id);
CREATE INDEX idx_invites_status_created_at ON project_invites(status, created_at);
