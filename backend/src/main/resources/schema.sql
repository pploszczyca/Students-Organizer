create table public.assignment_type
(
    id   UUID    NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL
);

create table public.material
(
    id   UUID    NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    url  VARCHAR NOT NULL
);

create table public.task
(
    id      UUID    NOT NULL PRIMARY KEY,
    name    VARCHAR NOT NULL,
    is_done BOOL    NOT NULL
)