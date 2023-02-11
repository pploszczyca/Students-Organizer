INSERT INTO student (id, name)
VALUES ('61b314ce-a9e7-11ed-afa1-0242ac120002', 'Pepe');

INSERT INTO term (id, number, year, student_id)
VALUES ('7ecd4d0e-a9e7-11ed-afa1-0242ac120002', 1, 2023,
        '61b314ce-a9e7-11ed-afa1-0242ac120002');

INSERT INTO subject (id, name, term_id)
VALUES ('a1e9f512-a9e7-11ed-afa1-0242ac120002', 'WDI', '7ecd4d0e-a9e7-11ed-afa1-0242ac120002');

INSERT INTO assignment_type (id, name) VALUES ('c0b071ba-a9e7-11ed-afa1-0242ac120002', 'Test');