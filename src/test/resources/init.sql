ALTER SESSION SET CONTAINER=XEPDB1;

CREATE TABLESPACE TESTCONTAINERS
    DATAFILE 'testcontainers.dbf'
    SIZE 1m;

ALTER USER test QUOTA UNLIMITED ON TESTCONTAINERS;

-- create table for TEST users (note prefix, as we're executing as 'system')

CREATE TABLE TEST.ConnectionTest(
    id number(10) NOT NULL,
    description NVARCHAR2(255) NOT NULL
);

INSERT INTO TEST.ConnectionTest(id, description) values (1, 'First Row');
INSERT INTO TEST.ConnectionTest(id, description) values (2, 'Second Row');