DROP SCHEMA IF EXISTS JMCS CASCADE;
CREATE SCHEMA JMCS;
SET search_path TO JMCS;

CREATE TABLE Person
(
    Person_ID  SERIAL PRIMARY KEY,
    First_Name VARCHAR(255),
    Last_Name  VARCHAR(255),
    BirthDate  DATE
);

CREATE TABLE Student
(
    Student_ID SERIAL PRIMARY KEY,
    Person_ID INTEGER NOT NULL REFERENCES Person (Person_ID)
);

CREATE TABLE Lecturer
(
    Lecturer_ID SERIAL PRIMARY KEY,
    Person_ID INTEGER NOT NULL REFERENCES Person (Person_ID)
);

CREATE TABLE University
(
    University_ID SERIAL PRIMARY KEY,
    Name          VARCHAR(255),
    City          VARCHAR(255)
);

CREATE TABLE Course
(
    Course_ID SERIAL PRIMARY KEY,
    Name      VARCHAR(255),
    ECTS      INTEGER
);

CREATE TABLE Exam
(
    Exam_ID       SERIAL PRIMARY KEY,
    Open_Date     DATE,
    Close_Date    DATE,
    Is_Repetition BOOLEAN,
    Date          DATE
);

CREATE TABLE Student_Register_Exam(
    Student_ID INTEGER NOT NULL REFERENCES Student (Student_ID),
    Exam_ID    INTEGER NOT NULL REFERENCES Exam (Exam_ID),
    PRIMARY KEY (Student_ID, Exam_ID)
);

CREATE TABLE CoursePart
(
    University_ID INTEGER NOT NULL REFERENCES University (University_ID),
    Course_ID     INTEGER NOT NULL REFERENCES Course (Course_ID),
    Lecturer_ID   INTEGER NOT NULL REFERENCES Lecturer (Lecturer_ID),
    Date          DATE,
    PRIMARY KEY (University_ID, Course_ID, Lecturer_ID)
);

CREATE TABLE Student_Follow_Course
(
    Student_ID INTEGER NOT NULL REFERENCES Student (Student_ID),
    Course_ID  INTEGER NOT NULL REFERENCES Course (Course_ID),
    PRIMARY KEY (Student_ID, Course_ID)
);

CREATE TABLE University_Host_Course
(
    University_ID         INTEGER NOT NULL REFERENCES University (University_ID),
    Course_ID             INTEGER NOT NULL REFERENCES Course (Course_ID),
    Exam_ID               INTEGER REFERENCES Exam (Exam_ID),
    Exam_Registration_URL TEXT,
    PRIMARY KEY (University_ID, Course_ID)
);
