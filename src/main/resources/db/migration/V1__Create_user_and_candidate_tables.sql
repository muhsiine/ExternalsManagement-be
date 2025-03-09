-- V1__Create_user_and_candidate_tables.sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- Create Users table
CREATE TABLE users
(
    id       UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Create Roles table (assumed for user roles)
CREATE TABLE roles
(
    id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create Users-Roles relation table
CREATE TABLE user_roles
(
    user_id UUID REFERENCES users (id),
    role_id UUID REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);

-- Create Candidates table
CREATE TABLE candidates
(
    id    UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50)
);

-- Create Experiences table (candidate's work experience)
CREATE TABLE experiences
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company      VARCHAR(255),
    position     VARCHAR(255),
    description  TEXT,
    candidate_id UUID REFERENCES candidates (id)
);

-- Create Skills table
CREATE TABLE skills
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name         VARCHAR(255),
    candidate_id UUID REFERENCES candidates (id)
);

-- Create Education table
CREATE TABLE education
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    degree       VARCHAR(255),
    institution  VARCHAR(255),
    year         VARCHAR(4),
    candidate_id UUID REFERENCES candidates (id)
);
