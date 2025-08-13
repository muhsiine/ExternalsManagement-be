-- DROP tables in order of dependencies
DROP TABLE IF EXISTS questions CASCADE;
DROP TABLE IF EXISTS answers CASCADE;
DROP TABLE IF EXISTS evaluations CASCADE;
DROP TABLE IF EXISTS evaluation_types CASCADE;
DROP TABLE IF EXISTS interviews CASCADE;
DROP TABLE IF EXISTS offers CASCADE;

-- Offers table
CREATE TABLE IF NOT EXISTS offers (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT
);

-- Interviews table
CREATE TABLE IF NOT EXISTS interviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    startTime TIMESTAMP,
    endTime TIMESTAMP,
    description TEXT,
    link VARCHAR(255),
    feedback_general VARCHAR(255),
    scheduled_at TIMESTAMP,
    comment VARCHAR(255),
    number_of_questions INTEGER,
    estimated_duration INTEGER,

    candidate_id UUID NOT NULL,
    offer_id UUID NOT NULL,

    CONSTRAINT fk_candidate
        FOREIGN KEY (candidate_id)
        REFERENCES candidates(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_offer
        FOREIGN KEY (offer_id)
        REFERENCES offers(id)
        ON DELETE CASCADE
);

-- Evaluation types
CREATE TABLE IF NOT EXISTS evaluation_types (
    id UUID PRIMARY KEY,
    description VARCHAR(100) NOT NULL,
    coefficient INTEGER
);

-- Evaluations
CREATE TABLE IF NOT EXISTS evaluations (
    id UUID PRIMARY KEY,
    score DOUBLE PRECISION,
    feedback TEXT,

    interview_id UUID NOT NULL,
    evaluation_type_id UUID NOT NULL,

    CONSTRAINT fk_interview_evaluation
        FOREIGN KEY (interview_id)
        REFERENCES interviews(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_evaluation_type
        FOREIGN KEY (evaluation_type_id)
        REFERENCES evaluation_types(id)
        ON DELETE CASCADE
);

-- Answers (now independent)
CREATE TABLE IF NOT EXISTS answers (
    id UUID PRIMARY KEY,
    description TEXT,
    duration_in_minutes INTEGER
);

-- Questions now have a foreign key to answers
CREATE TABLE IF NOT EXISTS questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    description TEXT,
    duration_in_minutes INTEGER,

    interview_id UUID NOT NULL,
    answer_id UUID,

    CONSTRAINT fk_interview
        FOREIGN KEY (interview_id)
        REFERENCES interviews(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_answer
        FOREIGN KEY (answer_id)
        REFERENCES answers(id)
        ON DELETE CASCADE
);
