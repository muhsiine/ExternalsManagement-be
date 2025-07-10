
-- Create offer table
CREATE TABLE IF NOT EXISTS offers (
    id UUID PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP,
    status VARCHAR(50),
    type VARCHAR(50),
    department VARCHAR(100)
);

-- Create interview table
CREATE TABLE IF NOT EXISTS interviews (
    id UUID PRIMARY KEY,
    offer_id UUID NOT NULL,
    candidate_id UUID NOT NULL,
    scheduled_date TIMESTAMP,
    status VARCHAR(50),
    CONSTRAINT fk_offer FOREIGN KEY (offer_id) REFERENCES offers(id) ON DELETE CASCADE,
    CONSTRAINT fk_candidate FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE
);

-- Create question table
CREATE TABLE IF NOT EXISTS questions (
    id UUID PRIMARY KEY,
    description TEXT,
    interview_id UUID NOT NULL,
    type VARCHAR(50),
    points INTEGER,
    question_order INTEGER,
    created_at TIMESTAMP,
    tags TEXT[],
    CONSTRAINT fk_interview FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE
);

-- Create response table


CREATE TABLE IF NOT EXISTS responses (
    id UUID PRIMARY KEY,
    description TEXT,
    question_id UUID NOT NULL,
    created_at TIMESTAMP,
    is_correct BOOLEAN,
    score FLOAT,
    CONSTRAINT fk_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);