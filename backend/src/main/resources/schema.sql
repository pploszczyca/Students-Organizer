CREATE TABLE public.assignment_type
(
    id   UUID    NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE public.material
(
    id   UUID    NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    url  VARCHAR NOT NULL
);

CREATE TABLE public.task
(
    id      UUID    NOT NULL PRIMARY KEY,
    name    VARCHAR NOT NULL,
    is_done BOOL    NOT NULL
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

CREATE TABLE public.assignment_task
(
    assignment_id UUID NOT NULL,
    task_id       UUID NOT NULL,

    CONSTRAINT fk_assignment_id
        FOREIGN KEY (assignment_id)
            REFERENCES assignment (id),

    CONSTRAINT fk_task_id
        FOREIGN KEY (task_id)
            REFERENCES task (id)
);

CREATE TABLE public.assignment_materials
(
    assignment_id UUID NOT NULL,
    material_id   UUID NOT NULL,

    CONSTRAINT fk_assignment_id
        FOREIGN KEY (assignment_id)
            REFERENCES assignment (id),

    CONSTRAINT fk_material_id
        FOREIGN KEY (material_id)
            REFERENCES material (id)
);