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
    end_date           TIMESTAMP NOT NULL,

    CONSTRAINT fk_assignment_type_id
        FOREIGN KEY (assignment_type_id)
            REFERENCES assignment_type (id)
);

CREATE TABLE public.material
(
    id   UUID    NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    url  VARCHAR NOT NULL,
    assignment_id UUID NOT NULL,

    CONSTRAINT fk_assignment_id
        FOREIGN KEY (assignment_id)
            REFERENCES assignment (id)
);

CREATE TABLE public.task
(
    id      UUID    NOT NULL PRIMARY KEY,
    name    VARCHAR NOT NULL,
    is_done BOOL    NOT NULL,
    assignment_id UUID NOT NULL,

    CONSTRAINT fk_assignment_id
        FOREIGN KEY (assignment_id)
            REFERENCES assignment (id)
);
