-- Users table
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

CREATE UNIQUE INDEX email_idx
  ON users (email);

-- Cities table
DROP TABLE IF EXISTS cities;
DROP SEQUENCE IF EXISTS cities_seq;

CREATE SEQUENCE cities_seq START 10000;

CREATE TABLE cities (
  id   INTEGER PRIMARY KEY DEFAULT nextval('cities_seq'),
  name TEXT NOT NULL
);

-- Group table
DROP TABLE IF EXISTS groups;
DROP SEQUENCE IF EXISTS group_seq;
DROP TYPE IF EXISTS GROUP_TYPE;

CREATE TYPE GROUP_TYPE AS ENUM ('registered', 'finished', 'current');

CREATE SEQUENCE group_seq START 10000;

CREATE TABLE groups (
  id         INTEGER PRIMARY KEY DEFAULT nextval('group_seq'),
  group_type GROUP_TYPE NOT NULL
);

-- Projects table
DROP TABLE IF EXISTS projects;
DROP SEQUENCE IF EXISTS projects_seq;

CREATE SEQUENCE projects_seq START 10000;

CREATE TABLE projects (
  id       INTEGER PRIMARY KEY DEFAULT nextval('projects_seq'),
  name     TEXT NOT NULL,
  group_id INTEGER REFERENCES groups (id)
);

-- Alter table users add references
ALTER TABLE users
  ADD COLUMN city_id INTEGER REFERENCES cities (id);