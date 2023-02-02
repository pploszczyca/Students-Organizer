create table public.assignment_type
(
    id   UUID    NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL
);

create table public.material
(
    id   UUID    NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    url  VARCHAR
);
