INSERT INTO student (id, name, password)
VALUES ('61b314ce-a9e7-11ed-afa1-0242ac120002', 'Pepe', '��-�m�');
INSERT INTO student (id, name, password)
VALUES ('c0782afb-78f5-4e79-81ed-2ae363be476b', 'Karol', '��-�m�');

INSERT INTO term (id, number, year, student_id)
VALUES ('7ecd4d0e-a9e7-11ed-afa1-0242ac120002', 1, 2023,
        '61b314ce-a9e7-11ed-afa1-0242ac120002');
INSERT INTO term (id, number, year, student_id)
VALUES ('8537e3a7-42c9-4b79-a0d2-a433adff2478', 2, 2024, 'c0782afb-78f5-4e79-81ed-2ae363be476b');

INSERT INTO subject (id, name, term_id)
VALUES ('a1e9f512-a9e7-11ed-afa1-0242ac120002', 'WDI', '7ecd4d0e-a9e7-11ed-afa1-0242ac120002');
INSERT INTO subject (id, name, term_id)
VALUES ('d3e77453-5641-4a44-a6cb-6822ede270a4', 'ASD', '8537e3a7-42c9-4b79-a0d2-a433adff2478');

INSERT INTO assignment_type (id, name)
VALUES ('c0b071ba-a9e7-11ed-afa1-0242ac120002', 'Test');