\i init_db.sql

INSERT INTO jmcs.person (first_name, last_name, birthdate) VALUES ('John', 'Doe', '1980-01-01');
INSERT INTO jmcs.person (first_name, last_name, birthdate) VALUES ('Mike', 'Smith', '1985-01-01');
INSERT INTO jmcs.person (first_name, last_name, birthdate) VALUES ('Jane', 'Deer', '1990-01-01');
INSERT INTO jmcs.person (first_name, last_name, birthdate) VALUES ('Sally', 'Jones', '1995-01-01');
INSERT INTO jmcs.person (first_name, last_name, birthdate) VALUES ('Bob', 'Brown', '2000-01-01');
INSERT INTO jmcs.person (first_name, last_name, birthdate) VALUES ('Joe', 'Black', '2005-01-01');

INSERT INTO jmcs.student (person_id) VALUES (4);
INSERT INTO jmcs.student (person_id) VALUES (5);
INSERT INTO jmcs.student (person_id) VALUES (6);

INSERT INTO jmcs.university (name, city) VALUES ('University of Bern', 'Bern');
INSERT INTO jmcs.university (name, city) VALUES ('University of Neuchatel', 'Neuchatel');
INSERT INTO jmcs.university (name, city) VALUES ('University of Fribourg', 'Fribourg');

INSERT INTO jmcs.course (name, ects) VALUES ('Applied Optimization', 6);
INSERT INTO jmcs.course (name, ects) VALUES ('Machine Learning', 6);
INSERT INTO jmcs.course (name, ects) VALUES ('Software Engineering', 6);
INSERT INTO jmcs.course (name, ects) VALUES ('Software Skills Lab', 5);

INSERT INTO jmcs.exam (open_date, close_date, is_repetition, date) VALUES ('2019-01-01', '2019-01-31', false, '2019-02-01');
INSERT INTO jmcs.exam (open_date, close_date, is_repetition, date) VALUES ('2019-02-01', '2019-02-28', false, '2019-03-01');
INSERT INTO jmcs.exam (open_date, close_date, is_repetition, date) VALUES ('2019-03-01', '2019-03-31', false, '2019-04-01');
INSERT INTO jmcs.exam (open_date, close_date, is_repetition, date) VALUES ('2019-04-01', '2019-04-30', false, '2019-05-01');

INSERT INTO jmcs.university_host_course (university_id, course_id, exam_id, exam_registration_url) VALUES (1, 1, 1, 'https://www.unibe.ch/applied_optimization');
INSERT INTO jmcs.university_host_course (university_id, course_id, exam_id, exam_registration_url) VALUES (2, 2, 2, 'https://www.unine.ch/machine_learning');
INSERT INTO jmcs.university_host_course (university_id, course_id, exam_id, exam_registration_url) VALUES (3, 3, 3, 'https://www.unifr.ch/software_engineering');
INSERT INTO jmcs.university_host_course (university_id, course_id, exam_id, exam_registration_url) VALUES (1, 4, 4, 'https://www.unibe.ch/software_skills_lab');

-- Student 1
INSERT INTO jmcs.student_follow_course (student_id, course_id) VALUES (1, 1);
INSERT INTO jmcs.student_follow_course (student_id, course_id) VALUES (1, 2);
-- Student 2
INSERT INTO jmcs.student_follow_course (student_id, course_id) VALUES (2, 3);
INSERT INTO jmcs.student_follow_course (student_id, course_id) VALUES (2, 4);
-- Student 3
INSERT INTO jmcs.student_follow_course (student_id, course_id) VALUES (3, 1);
