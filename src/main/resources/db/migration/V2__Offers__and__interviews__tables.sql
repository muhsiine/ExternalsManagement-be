-- drop all tables

DROP TABLE IF EXISTS responses CASCADE;
DROP TABLE IF EXISTS answers CASCADE;
DROP TABLE IF EXISTS questions CASCADE;
DROP TABLE IF EXISTS evaluations CASCADE;
DROP TABLE IF EXISTS evaluation_types CASCADE;
DROP TABLE IF EXISTS interviews CASCADE;
DROP TABLE IF EXISTS offers CASCADE;
CREATE TABLE IF NOT EXISTS offers (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT
);


CREATE TABLE IF NOT EXISTS interviews (
    id UUID PRIMARY KEY,
    offer_id UUID NOT NULL,
    candidate_id UUID,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    description TEXT,
    link VARCHAR(255),
    feedback_general TEXT,
    CONSTRAINT fk_offer FOREIGN KEY (offer_id) REFERENCES offers(id) ON DELETE CASCADE
    -- CONSTRAINT fk_candidate FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS evaluation_types (
    id UUID PRIMARY KEY,
    description VARCHAR(100) NOT NULL,
    coefficient INTEGER
);

CREATE TABLE IF NOT EXISTS evaluations (
    id UUID PRIMARY KEY,
    score DOUBLE PRECISION,
    feedback TEXT,
    interview_id UUID NOT NULL,
    evaluation_type_id UUID NOT NULL,
    CONSTRAINT fk_interview_evaluation FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_evaluation_type FOREIGN KEY (evaluation_type_id) REFERENCES evaluation_types(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS questions (
    id UUID PRIMARY KEY,
    description TEXT,
    interview_id UUID NOT NULL,
    duration_in_minutes INTEGER,
    CONSTRAINT fk_interview_question FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS answers (
    id UUID PRIMARY KEY,
    description TEXT,
    question_id UUID NOT NULL,
    duration_in_minutes INTEGER,
    CONSTRAINT fk_question_answer FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);
