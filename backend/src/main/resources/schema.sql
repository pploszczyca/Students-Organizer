CREATE TABLE public.student
(
    id       UUID    NOT NULL PRIMARY KEY,
    name     VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL
);

CREATE TABLE public.term
(
    id         UUID NOT NULL PRIMARY KEY,
    number     INT4 NOT NULL,
    year       INT4 NOT NULL,
    student_id UUID NOT NULL,

    CONSTRAINT fk_student_id
        FOREIGN KEY (student_id)
            REFERENCES student (id)
);

CREATE TABLE public.subject
(
    id      UUID    NOT NULL PRIMARY KEY,
    name    VARCHAR NOT NULL,
    term_id UUID    NOT NULL,

    CONSTRAINT fk_term_id
        FOREIGN KEY (term_id)
            REFERENCES term (id)
);

CREATE TABLE public.assignment_type
(
    id   UUID    NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE public.assignment
(
    id                 UUID      NOT NULL PRIMARY KEY,
    name               VARCHAR   NOT NULL,
    description        VARCHAR   NOT NULL,
    assignment_type_id UUID      NOT NULL,
    status             VARCHAR   NOT NULL,
    end_date           TIMESTAMP NOT NULL,
    subject_id         UUID      NOT NULL,

    CONSTRAINT fk_assignment_type_id
        FOREIGN KEY (assignment_type_id)
            REFERENCES assignment_type (id),

    CONSTRAINT fk_subject_id
        FOREIGN KEY (subject_id)
            REFERENCES subject (id)
);

CREATE TABLE public.material
(
    id            UUID    NOT NULL PRIMARY KEY,
    name          VARCHAR NOT NULL,
    url           VARCHAR NOT NULL,
    assignment_id UUID    NOT NULL,

    CONSTRAINT fk_assignment_id
        FOREIGN KEY (assignment_id)
            REFERENCES assignment (id)
);

CREATE TABLE public.task
(
    id            UUID    NOT NULL PRIMARY KEY,
    name          VARCHAR NOT NULL,
    is_done       BOOL    NOT NULL,
    assignment_id UUID    NOT NULL,

    CONSTRAINT fk_assignment_id
        FOREIGN KEY (assignment_id)
            REFERENCES assignment (id)
);
