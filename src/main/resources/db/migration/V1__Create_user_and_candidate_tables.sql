DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS candidates CASCADE;
DROP TABLE IF EXISTS contacts;
DROP TABLE IF EXISTS experiences;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS educations;
DROP TABLE IF EXISTS cv_files;
DROP TABLE IF EXISTS prompts;
DROP TABLE IF EXISTS country CASCADE;
DROP TABLE IF EXISTS city CASCADE;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS languages;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(255)        NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    role       VARCHAR(50)         NOT NULL, -- ADMIN, RECRUITER, CANDIDATE
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS candidates
(
    id         UUID PRIMARY KEY,
    full_name  VARCHAR(255),
    birth_date DATE,
    years_of_experience int,
    gender     VARCHAR(2),
    main_tech  VARCHAR(100), -- Main tech stack
    summary    TEXT
);

CREATE TABLE IF NOT EXISTS contacts
(
    id            UUID PRIMARY KEY,
    candidate_id  UUID REFERENCES candidates (id) ON DELETE CASCADE,
    contact_type  VARCHAR(255),
    contact_value VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS experiences
(
    id           UUID PRIMARY KEY,
    candidate_id UUID REFERENCES candidates (id) ON DELETE CASCADE,
    company_name VARCHAR(255),
    position     VARCHAR(255),
    start_date   VARCHAR(50),
    end_date     VARCHAR(50),
    description  TEXT
);

CREATE TABLE IF NOT EXISTS skills
(
    id                UUID PRIMARY KEY,
    candidate_id      UUID REFERENCES candidates (id) ON DELETE CASCADE,
    skill_name        VARCHAR(255),
    proficiency_level VARCHAR(50) -- Beginner, Intermediate, Expert
);

CREATE TABLE IF NOT EXISTS educations
(
    id           UUID PRIMARY KEY,
    candidate_id UUID REFERENCES candidates (id) ON DELETE CASCADE,
    institution  VARCHAR(255),
    degree       VARCHAR(255),
    start_date   VARCHAR(50),
    end_date     VARCHAR(50),
    diploma      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS cv_files
(
    id           UUID PRIMARY KEY,
    candidate_id UUID REFERENCES candidates (id) ON DELETE CASCADE,
    file_path    VARCHAR(500),
    file_type    VARCHAR(50),
    uploaded_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS prompts
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    prompt_code VARCHAR(255),
    prompt_desc TEXT,
    schema TEXT
);

-- Create Country Table
CREATE TABLE IF NOT EXISTS country
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name         VARCHAR(255) NOT NULL,
    english_name VARCHAR(255) NOT NULL UNIQUE

);

-- Create City Table
CREATE TABLE IF NOT EXISTS city
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       VARCHAR(255) NOT NULL UNIQUE,
    country_id UUID         NOT NULL REFERENCES country (id) ON DELETE CASCADE
);

-- Create Address Table
CREATE TABLE IF NOT EXISTS address
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    street       VARCHAR(255) NOT NULL,
    postal_code  VARCHAR(20),
    full_address VARCHAR(255),
    city_id      UUID         NOT NULL REFERENCES city (id) ON DELETE CASCADE,
    country_id   UUID         NOT NULL REFERENCES country (id) ON DELETE CASCADE,
    candidate_id UUID         NOT NULL UNIQUE REFERENCES candidates (id) ON DELETE CASCADE -- Link to Candidate
);

CREATE TABLE IF NOT EXISTS languages
(
    id                  UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    candidate_id        UUID         NOT NULL REFERENCES candidates (id) ON DELETE CASCADE, -- Link to Candidate
    description         TEXT NOT NULL,                                              -- Original language name
    english_description TEXT NOT NULL,                                              -- English name of the language
    full_description    TEXT,                                                       -- Full description of the language
    language            VARCHAR(100) NOT NULL,                                              -- Redundant field (can be same as 'description')
    language_in_english VARCHAR(100) NOT NULL,                                              -- Language name in English
    level               VARCHAR(50)  NOT NULL CHECK (level IN ('BEGINNER', 'LOWER_INTERMEDIATE' ,'INTERMEDIATE', 'UPPER_INTERMEDIATE', 'ADVANCED')),
    is_native           BOOLEAN      NOT NULL DEFAULT FALSE
);

INSERT INTO prompts (prompt_code, prompt_desc, schema) VALUES (
                                                                  'CV_EXTRACTION',
                                                                  'I''ll give you this cv/resume, please give me the candidate data, please try to put the maximum amount of data in the description fields,please take into account skill and natural languages rankings/levels could be  represented by either a star-based system or a progress bar, where the number of stars or the filled percentage of the progress bar indicates the proficiency level. The representation might be visually styled differently,but the number of stars or the progress bar''s filled portion remains the key to determining the level.It could be more complex than that, then you should interpret it visually the part around it to deduce the level.Try your best to deduce levels by any means visually contextually..',
                                                                  '{
                                                                "type": "object",
                                                                "properties": {
                                                                  "fullName": {
                                                                    "type": "string"
                                                                  },
                                                                  "birthDate": {
                                                                    "type": "string",
                                                                    "description": "the date should be in the form of YYYY-MM-DD"
                                                                  },
                                                                  "yearsOfExperience": {
                                                                    "type": "number"
                                                                  },
                                                                  "gender": {
                                                                    "type": "string",
                                                                    "enum": [
                                                                      "F",
                                                                      "M"
                                                                    ]
                                                                  },
                                                                  "summary": {
                                                                    "type": "string"
                                                                  },
                                                                  "mainTech": {
                                                                    "type": "string",
                                                                    "description": "try to deduce the mainTech from the whole CV"
                                                                  },
                                                                  "contacts": {
                                                                    "type": "array",
                                                                    "items": {
                                                                      "type": "object",
                                                                      "properties": {
                                                                        "contactType": {
                                                                          "type": "string",
                                                                          "enum": [
                                                                            "EMAIL",
                                                                            "PHONE",
                                                                            "LINKEDIN"
                                                                          ]
                                                                        },
                                                                        "contactValue": {
                                                                          "type": "string"
                                                                        }
                                                                      },
                                                                      "required": [
                                                                        "contactType",
                                                                        "contactValue"
                                                                      ]
                                                                    }
                                                                  },
                                                                  "experiences": {
                                                                    "type": "array",
                                                                    "items": {
                                                                      "type": "object",
                                                                      "properties": {
                                                                        "companyName": {
                                                                          "type": "string"
                                                                        },
                                                                        "position": {
                                                                          "type": "string"
                                                                        },
                                                                        "startDate": {
                                                                          "type": "string",
                                                                          "description": "the date should be in the form of YYYY-MM-DD"
                                                                        },
                                                                        "endDate": {
                                                                          "type": "string",
                                                                          "description": "the date should be in the form of YYYY-MM-DD"
                                                                        },
                                                                        "description": {
                                                                          "type": "string"
                                                                        }
                                                                      }
                                                                    }
                                                                  },
                                                                  "skills": {
                                                                    "type": "array",
                                                                    "items": {
                                                                      "type": "object",
                                                                      "properties": {
                                                                        "skillName": {
                                                                          "type": "string"
                                                                        },
                                                                        "proficiencyLevel": {
                                                                          "type": "string",
                                                                          "description": "default to BEGINNER",
                                                                          "enum": [
                                                                            "BEGINNER",
                                                                            "INTERMEDIATE",
                                                                            "EXPERT"
                                                                          ]
                                                                        }
                                                                      },
                                                                      "required": [
                                                                        "skillName",
                                                                        "proficiencyLevel"
                                                                      ]
                                                                    }
                                                                  },
                                                                  "educations": {
                                                                    "type": "array",
                                                                    "items": {
                                                                      "type": "object",
                                                                      "properties": {
                                                                        "institution": {
                                                                          "type": "string"
                                                                        },
                                                                        "diploma": {
                                                                          "type": "string"
                                                                        },
                                                                        "startDate": {
                                                                          "type": "string",
                                                                          "description": "the date should be in the form of YYYY-MM-DD"
                                                                        },
                                                                        "endDate": {
                                                                          "type": "string",
                                                                          "description": "the date should be in the form of YYYY-MM-DD"
                                                                        }
                                                                      }
                                                                    }
                                                                  },
                                                                  "address": {
                                                                    "type": "object",
                                                                    "properties": {
                                                                      "country": {
                                                                        "type": "object",
                                                                        "properties": {
                                                                          "name": {
                                                                            "type": "string"
                                                                          },
                                                                          "englishName": {
                                                                            "type": "string"
                                                                          }
                                                                        },
                                                                        "required": [
                                                                          "name",
                                                                          "englishName"
                                                                        ]
                                                                      },
                                                                      "city": {
                                                                        "type": "object",
                                                                        "properties": {
                                                                          "name": {
                                                                            "type": "string"
                                                                          }
                                                                        },
                                                                        "required": [
                                                                          "name"
                                                                        ]
                                                                      },
                                                                      "postalCode": {
                                                                        "type": "string",
                                                                        "description": "try you best to get it from the whole CV"
                                                                      },
                                                                      "street": {
                                                                        "type": "string"
                                                                      },
                                                                      "fullAddress": {
                                                                        "type": "string"
                                                                      }
                                                                    },
                                                                    "required": [
                                                                      "country",
                                                                      "city",
                                                                      "street"
                                                                    ]
                                                                  },
                                                                  "naturalLanguages": {
                                                                    "type": "array",
                                                                    "items": {
                                                                      "type": "object",
                                                                      "properties": {
                                                                        "language": {
                                                                          "type": "string"
                                                                        },
                                                                        "level": {
                                                                          "type": "string",
                                                                          "enum": [
                                                                            "BEGINNER",
                                                                            "LOWER_INTERMEDIATE",
                                                                            "INTERMEDIATE",
                                                                            "UPPER_INTERMEDIATE",
                                                                            "ADVANCED"
                                                                          ],
                                                                          "comment": "focus only in the part around the language, don''t consider the whole file, also consider (Between A1 and A2) or [*    ] -> BEGINNER, (Between A2 and B1) or [**   ]-> LOWER_INTERMEDIATE, (Between B1 and B2) or [***  ] -> INTERMEDIATE, (B2 and C1) or [**** ] -> UPPER_INTERMEDIATE, (More than C1 or native) or [*****] -> ADVANCED"
                                                                        },
                                                                        "englishDescription": {
                                                                          "type": "string"
                                                                        },
                                                                        "fullDescription": {
                                                                          "type": "string"
                                                                        },
                                                                        "description": {
                                                                          "type": "string"
                                                                        },
                                                                        "isNative": {
                                                                          "type": "boolean"
                                                                        },
                                                                        "languageInEnglish": {
                                                                          "type": "string"
                                                                        }
                                                                      },
                                                                      "required": [
                                                                        "language",
                                                                        "level",
                                                                        "englishDescription",
                                                                        "fullDescription",
                                                                        "description",
                                                                        "isNative",
                                                                        "languageInEnglish"
                                                                      ]
                                                                    }
                                                                  }
                                                                },
                                                                "required": [
                                                                  "fullName",
                                                                  "gender",
                                                                  "contacts",
                                                                  "skills",
                                                                  "address",
                                                                  "naturalLanguages"
                                                                ]
                                                              }'
                                                              ),(
                                                                  'INTERVIEW_EVALUATION_PROMPT',
                                                                  'You''re an expert interviewing manager and talent acquisition specialist.
                                                                            We''ve passed an interview for an #offer, to a #candidate, and we''ve gathered the information output and prepared a list of #Question/#answer from that interview,
                                                                            I will provide you below the needed information for them.
                                                                            Prepare a list of evaluations for that interview, each #evaluation_type is an entry in this list, I will also give you the list of #evaluation_types that we need to evaluate this candidate in.

                                                                            #Take in consideration these instructions:
                                                                             - The evaluations must be comprehensive and fair considering the job requirements in #offer_data and the #candidate_data
                                                                             - In relevance to the #evaluation_type being assessed, look in the #answers for technical accuracy, depth of knowledge, problem-solving approaches, and communication skills.
                                                                             - If the #answer is correct and the time of #answer is lower than the time given in the question, take it into account for positive assessment.
                                                                             - Cross-reference #candidate answers with #job requirements to ensure role-specific #evaluation.
                                                                             - Take evaluation type #coefficients in consideration
                                                                             - Ensure fairness by matching evaluation difficulty and accuracy to candidate''s stated experience level
                                                                             - Use the exact ''description'' value from each EvaluationTypes listFor the ''evaluationType'' field in the output.
                                                                             - Ensure each evaluation in the output array corresponds exactly to one EvaluationType from the input list.
                                                                             - Do not skip any evaluation types or add additional ones not provided in the Evaluation Types.
                                                                             - Reference specific technologies, skills, or experiences mentioned in the candidate profile when relevant.
                                                                             - Use simple language: A2-B1-B2
                                                                             - Return ONLY a valid JSON array with exactly this structure, no additional text or formatting: "{JSON_SCHEMA}", here you have a mock example: "{JSON_MOCK}".

                                                                            I provide bellow the needed information:
                                                                             - #Candidate Profile: "{CANDIDATE_DATA}",
                                                                             - #Job Offer requirements: "{OFFER_DATA}",
                                                                             - #Evaluation Types criteria: "{EVALUATION_TYPES_DATA}".
                                                                             - #Questions And Answers with the estimated answer time and the real answer time: "{QuestionAnswer_DATA}"',
                                                                  '[
                                                                {
                                                                  "score": "Double - between 0.00 and 100.00",
                                                                  "feedback": "String - Try to give an overall feedback of the performance of the candidate in this evaluation type",
                                                                  "evaluationTypeDescription": "String - use the exact ''description'' field value from the corresponding EvaluationType entity"
                                                                }
                                                              ]'
                                                              ),
                                                              (
                                                                  'INTERVIEW_QUESTION_GENERATION_PROMPT',
                                                                  'You are an expert interviewing manager and talent acquisition specialist. We need to prepare a list of interview Questions for candidates according to the candidates'' profile, Job Offer details, and the provided evaluation types .
                                                                            I will give you the information for the candidate''s profile, job offer requirements, and evaluation types criteria, generate tailored interview questions based on it.
                                                                            Generate exactly {NUMBER_OF_QUESTIONS
                                                                } interview questions.
                                                                            Take these Instructions into consideration:
                                                                            - Distribute the {NUMBER_OF_QUESTIONS
                                                                } questions across all evaluation types based on their coefficient weights. Higher coefficient evaluation types should receive proportionally more questions.
                                                                            - Ensure questions align with the candidate''s experience level, main technology, and the job requirements.
                                                                            - Each question should be relevant to both candidate''s background and job requirements.
                                                                            - Assign realistic time durations (typically 2-5 minutes per question), taking into account that the duration estimated for the interview is {ESTIMATED_DURATION
                                                                }.
                                                                            - Match question complexity to the candidate''s years of experience
                                                                            - Reference specific technologies, skills, or experiences mentioned in the candidate profile when relevant.
                                                                            - Use simple language: A2-B1-B2
                                                                            - Return ONLY a valid JSON array with exactly this structure, no additional text or formatting: "{JSON_SCHEMA}", here you have a mock example: "{JSON_MOCK}".
                                                                            Here below I provide the needed information:
                                                                            #Candidate Profile: "{CANDIDATE_DATA}",
                                                                            #Job Offer requirements: "{OFFER_DATA}",
                                                                            #Evaluation Types criteria: "{EVALUATION_TYPES_DATA}".',
                                                                  '[
                                                                  {
                                                                    "description": "string - The interview question text",
                                                                    "durationInMinutes": "integer - Duration in minutes (2-5)"
                                                                  }
                                                                ]'
                                                              );