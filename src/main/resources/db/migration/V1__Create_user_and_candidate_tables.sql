CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(255)        NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    role       VARCHAR(50)         NOT NULL, -- ADMIN, RECRUITER, CANDIDATE
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE candidates
(
    id         UUID PRIMARY KEY,
    full_name  VARCHAR(255),
    birth_date DATE,
    years_of_experience int,
    gender     VARCHAR(2),
    main_tech  VARCHAR(100), -- Main tech stack
    summary    TEXT
);

CREATE TABLE contacts
(
    id            UUID PRIMARY KEY,
    candidate_id  UUID REFERENCES candidates (id) ON DELETE CASCADE,
    contact_type  VARCHAR(255),
    contact_value VARCHAR(50)
);

CREATE TABLE experiences
(
    id           UUID PRIMARY KEY,
    candidate_id UUID REFERENCES candidates (id) ON DELETE CASCADE,
    company_name VARCHAR(255),
    position     VARCHAR(255),
    start_date   VARCHAR(50),
    end_date     VARCHAR(50),
    description  TEXT
);

CREATE TABLE skills
(
    id                UUID PRIMARY KEY,
    candidate_id      UUID REFERENCES candidates (id) ON DELETE CASCADE,
    skill_name        VARCHAR(255),
    proficiency_level VARCHAR(50) -- Beginner, Intermediate, Expert
);

CREATE TABLE educations
(
    id           UUID PRIMARY KEY,
    candidate_id UUID REFERENCES candidates (id) ON DELETE CASCADE,
    institution  VARCHAR(255),
    degree       VARCHAR(255),
    start_date   VARCHAR(50),
    end_date     VARCHAR(50),
    diploma      VARCHAR(255)
);

CREATE TABLE cv_files
(
    id           UUID PRIMARY KEY,
    candidate_id UUID REFERENCES candidates (id) ON DELETE CASCADE,
    file_path    VARCHAR(500),
    file_type    VARCHAR(50),
    uploaded_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE prompts
(
    id          UUID PRIMARY KEY,
    prompt_code VARCHAR(255),
    prompt_desc TEXT
);

-- Create Country Table
CREATE TABLE country
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name         VARCHAR(255) NOT NULL,
    english_name VARCHAR(255) NOT NULL UNIQUE

);

-- Create City Table
CREATE TABLE city
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       VARCHAR(255) NOT NULL UNIQUE,
    country_id UUID         NOT NULL REFERENCES country (id) ON DELETE CASCADE
);

-- Create Address Table
CREATE TABLE address
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    street       VARCHAR(255) NOT NULL,
    postal_code  VARCHAR(20),
    full_address VARCHAR(255),
    city_id      UUID         NOT NULL REFERENCES city (id) ON DELETE CASCADE,
    country_id   UUID         NOT NULL REFERENCES country (id) ON DELETE CASCADE,
    candidate_id UUID         NOT NULL UNIQUE REFERENCES candidates (id) ON DELETE CASCADE -- Link to Candidate
);

CREATE TABLE languages
(
    id                  UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    candidate_id        UUID         NOT NULL REFERENCES candidates (id) ON DELETE CASCADE, -- Link to Candidate
    description         TEXT NOT NULL,                                              -- Original language name
    english_description TEXT NOT NULL,                                              -- English name of the language
    full_description    TEXT,                                                       -- Full description of the language
    language            VARCHAR(100) NOT NULL,                                              -- Redundant field (can be same as 'description')
    language_in_english VARCHAR(100) NOT NULL,                                              -- Language name in English
    level               VARCHAR(50)  NOT NULL CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'FLUENT', 'NATIVE')),
    is_native           BOOLEAN      NOT NULL DEFAULT FALSE
);
