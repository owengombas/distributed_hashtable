SET search_path TO jmcs;
SELECT person.first_name,
       person.last_name,
       course.name,
       university.name,
       university_host_course.exam_registration_url,
       exam.open_date,
       exam.close_date
FROM student
         INNER JOIN person ON student.person_id = person.person_id
         INNER JOIN student_follow_course ON student.student_id = student_follow_course.student_id
         INNER JOIN course ON student_follow_course.course_id = course.course_id
         INNER JOIN university_host_course ON course.course_id = university_host_course.course_id
         INNER JOIN university ON university_host_course.university_id = university.university_id
         INNER JOIN exam ON university_host_course.exam_id = exam.exam_id
WHERE course.name = 'Software Skills Lab';
