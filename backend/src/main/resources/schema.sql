create table public.assignment_type
(
    id   SERIAL  NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL
);

create table public.material
(
    id   SERIAL  NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    url  VARCHAR
);
