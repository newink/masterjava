DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS user_seq;
DROP TYPE IF EXISTS USER_FLAG;

CREATE TYPE USER_FLAG AS ENUM ('active', 'deleted', 'superuser');

CREATE SEQUENCE user_seq START 100000;

CREATE TABLE users (
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  full_name TEXT      NOT NULL,
  email     TEXT      NOT NULL,
  flag      USER_FLAG NOT NULL
);

