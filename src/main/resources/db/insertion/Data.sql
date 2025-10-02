CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- countries
INSERT INTO country (id, name, english_name)
VALUES (uuid_generate_v4(), 'Maroc', 'Morocco')
    ON CONFLICT (english_name) DO NOTHING;

WITH morocco_id AS (SELECT id FROM country WHERE english_name = 'Morocco')
INSERT INTO city (id, name, country_id)
SELECT
    uuid_generate_v4(),
    city_name,
    (SELECT id FROM morocco_id)
FROM (VALUES
          ('Casablanca'), ('Rabat'), ('Marrakech'), ('Fes'), ('Tangier'),
          ('Agadir'), ('Tetouan'), ('Oujda'), ('Safi'), ('Kenitra'),
          ('Essaouira'), ('Nador'), ('Chefchaouen'), ('Meknes'), ('Salé'),
          ('Taza'), ('Khouribga'), ('El Jadida'), ('Beni Mellal'), ('Tiznit')
     ) AS cities(city_name)
    ON CONFLICT (name) DO NOTHING;

-- candidates
INSERT INTO candidates (id, full_name, birth_date, years_of_experience, gender, main_tech, summary)
SELECT
    uuid_generate_v4(),
    first_name || ' ' || last_name,
    DATE '1990-01-01' - (FLOOR(RANDOM() * 365 * 25) || ' days')::INTERVAL,
    FLOOR(RANDOM() * 7) + 2, -- 2–8 years
    gender,
    (ARRAY['Java','Python','React','Angular'])[FLOOR(RANDOM()*4)+1],
    (ARRAY[
        'Full-stack developer with strong problem-solving skills',
        'Passionate about clean code and agile methodologies',
        'DevOps specialist focused on automation and CI/CD',
        'Frontend expert with UX/UI design experience',
        'Backend engineer specializing in microservices',
        'Data-driven developer with ML experience',
        'Cloud infrastructure architect and developer',
        'Mobile developer with cross-platform expertise'
    ])[FLOOR(RANDOM()*8)+1]
FROM (
    VALUES
    ('Youssef', 'Alami', 'M'), ('Fatima', 'Zahra', 'F'), ('Mehdi', 'Benjelloun', 'M'),
    ('Nadia', 'Cherkaoui', 'F'), ('Karim', 'El Mansouri', 'M'), ('Leila', 'Bennani', 'F'),
    ('Omar', 'Tazi', 'M'), ('Samira', 'Rahmani', 'F'), ('Bilal', 'Khalil', 'M'),
    ('Hind', 'Daoudi', 'F'), ('Adil', 'Chraibi', 'M'), ('Soukaina', 'Idrissi', 'F'),
    ('Anas', 'Mourad', 'M'), ('Noura', 'Belhaj', 'F'), ('Hicham', 'Lahlou', 'M'),
    ('Asma', 'Bouzidi', 'F'), ('Rachid', 'Ouazzani', 'M'), ('Salma', 'Toumi', 'F'),
    ('Jamal', 'Zahir', 'M'), ('Imane', 'Boutaleb', 'F'), ('Nabil', 'Amrani', 'M'),
    ('Khadija', 'Safiani', 'F'), ('Yassine', 'Bakkal', 'M'), ('Hanane', 'El Kaddouri', 'F'),
    ('Reda', 'Hassani', 'M'), ('Meryem', 'Berrada', 'F'), ('Said', 'Fassi', 'M'),
    ('Najat', 'Laraki', 'F'), ('Khalid', 'Sbai', 'M'), ('Zineb', 'Mernissi', 'F'),
    ('Amine', 'Barakat', 'M'), ('Sanaa', 'Moujtahid', 'F'), ('Hamza', 'Qasri', 'M'),
    ('Ghita', 'Akkaoui', 'F'), ('Ayoub', 'Fahmi', 'M'), ('Rim', 'Mazini', 'F'),
    ('Tarik', 'Rochdi', 'M'), ('Lina', 'Benaissa', 'F'), ('Fouad', 'Temsamani', 'M'),
    ('Houda', 'Makhfi', 'F'), ('Marouane', 'Mabrouk', 'M'), ('Nihal', 'Saidi', 'F')
    ) AS names(first_name, last_name, gender);

-- address
INSERT INTO address (id, street, postal_code, full_address, city_id, country_id, candidate_id)
SELECT
    uuid_generate_v4(),
    street_type || ' ' || street_name,
    LPAD(FLOOR(RANDOM() * 100000)::TEXT, 5, '0'),
    street_type || ' ' || street_name || ', ' || c.name,
    c.id,
    (SELECT id FROM country WHERE english_name = 'Morocco'),
    cand.id
FROM candidates cand
         CROSS JOIN LATERAL (
    SELECT
        CASE
            WHEN RANDOM() < 0.3 THEN 'Rue'
            WHEN RANDOM() < 0.6 THEN 'Avenue'
            ELSE 'Boulevard'
            END AS street_type,
        (ARRAY['Mohammed V','Hassan II','Al Massira','Moulay Ismail','Oqba Ibn Nafia',
         'Mehdi Ben Barka','Abdelkrim Khattabi','Al Fadila','Ibn Sina','Al Maghrib Al Arabi'])[FLOOR(RANDOM()*10)+1] AS street_name
    ) st
    CROSS JOIN LATERAL (
SELECT id, name FROM city ORDER BY RANDOM() LIMIT 1
    ) c
WHERE NOT EXISTS (SELECT 1 FROM address a WHERE a.candidate_id = cand.id);

-- contacts
INSERT INTO contacts (id, candidate_id, contact_type, contact_value)
SELECT uuid_generate_v4(), id, 'Email',
       LOWER(SPLIT_PART(full_name, ' ', 1) || '.' || SPLIT_PART(full_name, ' ', 2) || FLOOR(RANDOM()*10)::TEXT || '@domain.ma')
FROM candidates
UNION ALL
SELECT uuid_generate_v4(), id, 'Phone', '+2126' || LPAD(FLOOR(RANDOM()*100000000)::TEXT, 8, '0')
FROM candidates;

-- experiences
INSERT INTO experiences (id, candidate_id, company_name, position, start_date, end_date, description)
SELECT
    uuid_generate_v4(),
    id,
    (ARRAY['OCP Group','Attijariwafa Bank','Maroc Telecom','ONCF','Royal Air Maroc',
     'Saham Assurance','Yazaki Morocco','Managem','SNTL','Cosumar',
     'BMCE Bank','LafargeHolcim','Intelcia','Dell Technologies Morocco',
     'IBM Morocco','Capgemini Morocco','HPS Morocco','Inwi','Orange Morocco'])[FLOOR(RANDOM()*19)+1],
    (ARRAY['Software Engineer','Senior Developer','DevOps Specialist','Tech Lead',
           'Full Stack Developer','Data Engineer','Mobile Developer','Cloud Architect'])[FLOOR(RANDOM()*8)+1],
    CURRENT_DATE - (years_of_experience + FLOOR(RANDOM()*3) || ' years')::INTERVAL,
    CASE WHEN RANDOM() > 0.3 THEN CURRENT_DATE - (FLOOR(RANDOM()*12) || ' months')::INTERVAL END,
    (ARRAY[
        'Developed scalable solutions for enterprise clients',
        'Led team in agile development environment',
        'Implemented CI/CD pipelines and cloud infrastructure',
        'Optimized system performance and reduced costs',
        'Created mobile applications with 100k+ downloads',
        'Designed microservices architecture',
        'Mentored junior developers and conducted code reviews'
    ])[FLOOR(RANDOM()*7)+1]
FROM candidates;

-- skills
INSERT INTO skills (id, candidate_id, skill_name, proficiency_level)
WITH candidate_skills AS (
    SELECT
        id,
        (ARRAY['Java','React','Angular','Python','JavaScript','Laravel','.NET','C#'])[FLOOR(RANDOM()*8)+1] AS skill
FROM candidates
    CROSS JOIN generate_series(1, (FLOOR(RANDOM()*5)+3)::integer)
    )
SELECT
    uuid_generate_v4(),
    id,
    skill,
    CASE
        WHEN RANDOM() < 0.2 THEN 'BEGINNER'
        WHEN RANDOM() < 0.6 THEN 'INTERMEDIATE'
        ELSE 'EXPERT'
        END
FROM candidate_skills;

-- educations
INSERT INTO educations (id, candidate_id, institution, degree, start_date, end_date, diploma)
SELECT
    uuid_generate_v4(),
    id,
    (ARRAY[
         'Université Mohammed V Rabat','Université Hassan II Casablanca',
     'Université Cadi Ayyad Marrakech','Al Akhawayn University',
     'EMI Rabat','ENSET Mohammedia','EHTP Casablanca','INPT Rabat',
     'ENSIAS Rabat','Université Ibn Tofail Kénitra'
         ])[FLOOR(RANDOM()*10)+1],
    (ARRAY['Bachelor','Master','Engineering Diploma','PhD'])[FLOOR(RANDOM()*4)+1],
    CURRENT_DATE - (6 + FLOOR(RANDOM()*5) || ' years')::INTERVAL,
    CURRENT_DATE - (FLOOR(RANDOM()*3) || ' years')::INTERVAL,
    (ARRAY[
        'Computer Science','Software Engineering','Information Systems',
        'Data Science','Electrical Engineering','AI & Machine Learning'
    ])[FLOOR(RANDOM()*6)+1]
FROM candidates;

-- languages (force English + random others)
-- English (ADVANCED, native) for every candidate
INSERT INTO languages (id, candidate_id, description, english_description, full_description, language, language_in_english, level, is_native)
SELECT
    uuid_generate_v4(), id, 'English', 'English', 'English Language', 'English', 'English', 'ADVANCED', true
FROM candidates;

-- Additional random languages
INSERT INTO languages (id, candidate_id, description, english_description, full_description, language, language_in_english, level, is_native)
WITH language_data AS (
    SELECT *, ROW_NUMBER() OVER () - 1 AS row_number
    FROM (VALUES
              ('العربية', 'Arabic', 'اللغة العربية الفصحى', 'Arabic', 'Arabic'),
              ('Français', 'French', 'Langue Française', 'French', 'French'),
              ('English', 'English', 'English Language', 'English', 'English'),
              ('Español', 'Spanish', 'Idioma Español', 'Spanish', 'Spanish')
         ) AS langs (description, english_description, full_description, language, language_in_english)
),
     candidate_languages AS (
         SELECT id AS candidate_id, (FLOOR(RANDOM() * 2))::integer AS num_languages
         FROM candidates
     )
SELECT
    uuid_generate_v4(),
    c.candidate_id,
    ld.description,
    ld.english_description,
    ld.full_description,
    ld.language,
    ld.language_in_english,
    (ARRAY['BEGINNER','LOWER_INTERMEDIATE','INTERMEDIATE','UPPER_INTERMEDIATE','ADVANCED'])[FLOOR(RANDOM()*5)+1],
    (RANDOM() < 0.2)
FROM candidate_languages c
    JOIN language_data ld ON RANDOM() < 0.5
WHERE c.num_languages > 0;

-- OFFERS (aligned mainTech + biased languages)
INSERT INTO offers (id, title, description, formatted_description)
SELECT
    uuid_generate_v4(),
    (ARRAY[
         'Junior Java Developer','Senior Frontend Engineer','DevOps Specialist',
     'Full Stack Developer','Data Scientist','Mobile App Developer',
     'Cloud Solutions Architect','Backend Engineer'
         ])[FLOOR(RANDOM() * 8) + 1],
    (ARRAY[
        'Work on exciting projects with international clients.',
        'Lead frontend development teams and improve UI/UX.',
        'Manage CI/CD pipelines and automate deployments.',
        'Develop and maintain full stack applications.',
        'Analyze large datasets and build ML models.',
        'Create cross-platform mobile applications.',
        'Design and implement scalable cloud infrastructure.',
        'Build robust backend services and APIs.'
    ])[FLOOR(RANDOM() * 8) + 1],
    CAST(
        json_build_object(
            'description', 'We are seeking an experienced, highly motivated, and technically proficient Senior Java Developer to join our growing technology team. The ideal candidate will have a deep understanding of Java development and a passion for building scalable, high-performance, and reliable software applications. You will play a critical role in designing, developing, and maintaining enterprise-level solutions that power our business operations and drive innovation.
                            In this role, you will be responsible for collaborating with cross-functional teams to analyze requirements, create technical specifications, and implement end-to-end solutions. You will ensure code quality, maintainability, and adherence to best practices while mentoring junior developers and contributing to architectural decisions. Your work will directly impact the performance, scalability, and reliability of our software products.',
            'mainTech', (ARRAY['Java','Python','React','Angular'])[FLOOR(RANDOM()*4)+1],
            'skills', (ARRAY[
                'Java - Spring Boot - Docker - Kubernetes - AWS',
                'React - Node.js - MongoDB - Docker - Git',
                'Python - Django - PostgresSQL - Kubernetes - AWS',
                'C# - .NET - SQL Server - Azure - Agile'
            ])[FLOOR(RANDOM() * 4) + 1],
            'languages', json_build_array(
                json_build_object('languageName', 'English', 'level', 'INTERMEDIATE'),
                json_build_object('languageName', (ARRAY['French','Spanish'])[FLOOR(RANDOM()*2)+1], 'level', 'ADVANCED')
            ),
            'yearsOfExperience', FLOOR(RANDOM() * 5) + 2,
            'mainResponsibilities', 'Design and implement scalable systems...',
            'education', 'Bachelor in Computer Science - Master in Software Engineering',
            'keywords', 'Java - Spring Boot - Microservices - Docker - Kubernetes - Agile - DevOps - AWS - Backend Development'
        ) AS text
    )
FROM generate_series(1, 10);

-- Records
INSERT INTO recording (id, recorded_at, file_url, transcript)
SELECT
    uuid_generate_v4(),
    CURRENT_TIMESTAMP - (FLOOR(RANDOM() * 30) || ' days')::INTERVAL,
    'https://storage.example.com/recordings/' || uuid_generate_v4()::TEXT || '.mp3',
    '[' || string_agg(
            json_build_object(
                    'Question', questions[FLOOR(RANDOM() * array_length(questions,1) + 1)::INT],
                    'QuestionTime', (FLOOR(RANDOM() * 60) || ':' || FLOOR(RANDOM() * 60) || ':' || FLOOR(RANDOM() * 60)),
                    'Answer', answers[FLOOR(RANDOM() * array_length(answers,1) + 1)::INT],
                    'AnswerTime', (FLOOR(RANDOM() * 60) || ':' || FLOOR(RANDOM() * 60) || ':' || FLOOR(RANDOM() * 60))
            )::TEXT,
            ','
           ) || ']'
FROM generate_series(1,20) AS gs,
     LATERAL (
              SELECT ARRAY[
                         'What is your name?',
                     'Where are you from?',
                     'What do you do?',
                     'How was your day?',
                     'Describe your experience.',
                     'Tell me about your hobbies.',
                     'What is your favorite book?',
                     'What are your goals?'
    ] AS questions,
    ARRAY[
    'My name is John.',
    'I am from Morocco.',
    'I work as an engineer.',
    'It was great!',
    'I had an amazing experience.',
    'I enjoy reading.',
    'I love science fiction.',
    'My goal is to become a developer.'
    ] AS answers
    ) AS q_and_a;

-- INTERVIEWS - FIXED VERSION WITH UNIQUE DESCRIPTIONS
WITH offer_candidates AS (
    SELECT
        o.id as offer_id,
        o.title as offer_title,
        c.id as candidate_id,
        c.full_name as candidate_name,
        ROW_NUMBER() OVER (ORDER BY RANDOM()) as rn
    FROM offers o
    CROSS JOIN candidates c
    ORDER BY RANDOM()
    LIMIT 20
),
interview_details AS (
    SELECT
        oc.*,
        CURRENT_TIMESTAMP - (FLOOR(RANDOM() * 30) || ' days')::INTERVAL AS starttime,
        (30 + FLOOR(RANDOM() * 60)) * INTERVAL '1 minute' AS duration,
        r.id as recording_id,
        -- Generate unique interview descriptions
        CASE
            WHEN oc.rn % 8 = 1 THEN 'Technical Interview - ' || oc.offer_title || ' position with ' || oc.candidate_name
            WHEN oc.rn % 8 = 2 THEN 'First Round Interview - ' || oc.candidate_name || ' for ' || oc.offer_title
            WHEN oc.rn % 8 = 3 THEN 'HR Screening - ' || oc.offer_title || ' candidate assessment'
            WHEN oc.rn % 8 = 4 THEN 'Final Interview - ' || oc.candidate_name || ' final evaluation'
            WHEN oc.rn % 8 = 5 THEN 'Phone Interview - Initial screening for ' || oc.offer_title
            WHEN oc.rn % 8 = 6 THEN 'Panel Interview - ' || oc.candidate_name || ' team assessment'
            WHEN oc.rn % 8 = 7 THEN 'Behavioral Interview - ' || oc.offer_title || ' cultural fit evaluation'
            ELSE 'Follow-up Interview - ' || oc.candidate_name || ' second round discussion'
        END as interview_description,
        -- Generate varied comments
        (ARRAY[
            'Candidate showed strong technical skills and communication',
            'Good cultural fit, needs technical assessment follow-up',
            'Excellent problem-solving abilities demonstrated',
            'Strong background but limited in required technology',
            'Impressive portfolio and project experience',
            'Good teamwork skills, moderate technical knowledge',
            'Outstanding communication and leadership potential',
            'Solid technical foundation with room for growth',
            'Exceptional analytical thinking and creativity',
            'Great enthusiasm and willingness to learn'
        ])[FLOOR(RANDOM() * 10) + 1] as interview_comment
    FROM offer_candidates oc
    CROSS JOIN LATERAL (
        SELECT id FROM recording ORDER BY RANDOM() LIMIT 1
    ) r
)
INSERT INTO interviews (
    id,
    offer_id,
    candidate_id,
    starttime,
    endtime,
    description,
    link,
    feedback_general,
    scheduled_at,
    comment,
    number_of_questions,
    estimated_duration,
    recording_id
)
SELECT
    uuid_generate_v4(),
    id.offer_id,
    id.candidate_id,
    id.starttime,
    id.starttime + id.duration AS endtime,
    id.interview_description,  -- Now unique for each interview
    'https://meetings.example.com/' || uuid_generate_v4()::TEXT,
    NULL,
    CURRENT_TIMESTAMP + (FLOOR(RANDOM() * 10) || ' days')::INTERVAL,
    id.interview_comment,  -- Varied comments
    FLOOR(RANDOM() * 10) + 10,  -- 10-19 questions
    FLOOR(RANDOM() * 30) + 45,  -- 45-74 minutes
    id.recording_id
FROM interview_details id;

-- EVALUATION_TYPES
INSERT INTO evaluation_types (id, description, coefficient)
SELECT
    uuid_generate_v4(),
    description,
    CASE description
        WHEN 'Technical Skills' THEN 3
        WHEN 'Communication' THEN 2
        WHEN 'Problem Solving' THEN 3
        WHEN 'Cultural Fit' THEN 1
        WHEN 'Experience' THEN 2
        WHEN 'Motivation' THEN 1
        ELSE 1
        END
FROM (VALUES
          ('Technical Skills'),
          ('Communication'),
          ('Problem Solving'),
          ('Cultural Fit'),
          ('Experience'),
          ('Motivation')
     ) AS t(description);

-- EVALUATIONS
WITH interview_evaluation_combinations AS (
    SELECT
        i.id as interview_id,
        et.id as evaluation_type_id,
        et.description as eval_type
    FROM interviews i
    CROSS JOIN evaluation_types et
    WHERE RANDOM() > 0.3  -- Not all combinations, create some variety
)
INSERT INTO evaluations (id, score, feedback, interview_id, evaluation_type_id)
SELECT
    uuid_generate_v4(),
    CASE
        WHEN iec.eval_type = 'Technical Skills' THEN ROUND((RANDOM() * 40 + 40)::NUMERIC, 2)  -- 40-80 range
        WHEN iec.eval_type = 'Communication' THEN ROUND((RANDOM() * 30 + 50)::NUMERIC, 2)     -- 50-80 range
        WHEN iec.eval_type = 'Problem Solving' THEN ROUND((RANDOM() * 35 + 45)::NUMERIC, 2)   -- 45-80 range
        ELSE ROUND((RANDOM() * 40 + 50)::NUMERIC, 2)  -- 50-90 range for other skills
    END,
    CASE iec.eval_type
        WHEN 'Technical Skills' THEN
            (ARRAY[
                'Strong Java and Spring Boot knowledge',
                'Good understanding of microservices architecture',
                'Needs improvement in database design',
                'Excellent coding practices and clean code',
                'Limited experience with cloud technologies',
                'Outstanding debugging and problem-solving skills',
                'Good grasp of testing frameworks and TDD',
                'Needs more experience with DevOps practices'
            ])[FLOOR(RANDOM() * 8) + 1]
        WHEN 'Communication' THEN
            (ARRAY[
                'Excellent verbal communication skills',
                'Clear and concise explanations',
                'Good active listening abilities',
                'Needs improvement in presentation skills',
                'Strong interpersonal skills',
                'Confident and articulate responses',
                'Good at asking clarifying questions',
                'Effective in explaining technical concepts'
            ])[FLOOR(RANDOM() * 8) + 1]
        WHEN 'Problem Solving' THEN
            (ARRAY[
                'Systematic approach to problem solving',
                'Creative thinking and innovative solutions',
                'Good analytical and logical reasoning',
                'Needs to break down complex problems better',
                'Strong debugging and troubleshooting skills',
                'Good at identifying root causes',
                'Effective in handling challenging scenarios',
                'Quick learner with good adaptation skills'
            ])[FLOOR(RANDOM() * 8) + 1]
        WHEN 'Cultural Fit' THEN
            (ARRAY[
                'Great team player with collaborative spirit',
                'Strong alignment with company values',
                'Good cultural fit for agile environment',
                'Positive attitude and growth mindset',
                'Strong work ethic and dedication',
                'Good fit for remote/hybrid work culture',
                'Excellent interpersonal skills with team',
                'Shows initiative and proactive approach'
            ])[FLOOR(RANDOM() * 8) + 1]
        WHEN 'Experience' THEN
            (ARRAY[
                'Solid experience in enterprise applications',
                'Good background in startup environment',
                'Relevant project experience in similar domain',
                'Limited but promising career progression',
                'Strong portfolio of completed projects',
                'Good mix of frontend and backend experience',
                'Valuable experience in team leadership',
                'Impressive internship and academic projects'
            ])[FLOOR(RANDOM() * 8) + 1]
        ELSE
            (ARRAY[
                'Highly motivated and enthusiastic',
                'Strong desire for continuous learning',
                'Clear career goals and aspirations',
                'Good understanding of role expectations',
                'Passionate about technology and innovation',
                'Shows commitment to professional growth',
                'Eager to contribute to team success',
                'Demonstrates self-motivation and drive'
            ])[FLOOR(RANDOM() * 8) + 1]
    END,
    iec.interview_id,
    iec.evaluation_type_id
FROM interview_evaluation_combinations iec;

-- QUESTIONS - Create varied questions for each interview
WITH interview_question_pools AS (
    SELECT
        i.id as interview_id,
        o.title as position_title,
        -- Different question sets based on position
        CASE
            WHEN o.title LIKE '%Java%' OR o.title LIKE '%Backend%' THEN
                ARRAY[
                    'Explain the difference between String, StringBuilder, and StringBuffer in Java',
                    'How do you handle exceptions in Spring Boot applications?',
                    'Describe your experience with microservices architecture',
                    'What is dependency injection and how does Spring implement it?',
                    'How do you optimize database queries in your applications?',
                    'Explain the concept of RESTful web services',
                    'How do you implement security in Spring Boot applications?',
                    'Describe your testing strategy for backend applications'
                ]
            WHEN o.title LIKE '%Frontend%' OR o.title LIKE '%React%' OR o.title LIKE '%Angular%' THEN
                ARRAY[
                    'Explain the virtual DOM concept in React',
                    'How do you manage state in large React applications?',
                    'What are React hooks and how do you use them?',
                    'Describe your approach to responsive web design',
                    'How do you optimize frontend application performance?',
                    'Explain the difference between controlled and uncontrolled components',
                    'How do you handle API integration in frontend applications?',
                    'Describe your experience with CSS preprocessors and frameworks'
                ]
            WHEN o.title LIKE '%DevOps%' THEN
                ARRAY[
                    'Explain the CI/CD pipeline you have implemented',
                    'How do you monitor application performance in production?',
                    'Describe your experience with containerization using Docker',
                    'How do you implement infrastructure as code?',
                    'What is your approach to automated testing in DevOps?',
                    'Explain blue-green deployment strategy',
                    'How do you handle secrets management in applications?',
                    'Describe your experience with cloud platforms (AWS, Azure, GCP)'
                ]
            WHEN o.title LIKE '%Full Stack%' THEN
                ARRAY[
                    'Describe a full-stack application you have developed',
                    'How do you ensure consistency between frontend and backend?',
                    'Explain your database design approach for web applications',
                    'How do you handle authentication and authorization?',
                    'Describe your API design principles',
                    'How do you manage data flow in full-stack applications?',
                    'What is your approach to code organization in full-stack projects?',
                    'How do you handle real-time features in web applications?'
                ]
            ELSE
                ARRAY[
                    'Tell me about a challenging project you worked on',
                    'How do you stay updated with new technologies?',
                    'Describe a time when you had to debug a complex issue',
                    'How do you approach code reviews?',
                    'What is your experience working in agile environments?',
                    'How do you handle tight deadlines and pressure?',
                    'Describe your collaboration style with team members',
                    'What motivates you in software development?'
                ]
        END as question_pool
    FROM interviews i
    JOIN offers o ON i.offer_id = o.id
),
interview_questions AS (
    SELECT
        iqp.interview_id,
        iqp.question_pool[FLOOR(RANDOM() * array_length(iqp.question_pool, 1)) + 1] as question_text,
        generate_series(1, (FLOOR(RANDOM() * 4) + 3)::integer) as question_num  -- 3-6 questions per interview
    FROM interview_question_pools iqp
)
INSERT INTO questions (id, description, duration_in_minutes, interview_id, answer_id)
SELECT
    uuid_generate_v4(),
    iq.question_text,
    (ARRAY[5, 8, 10, 12, 15, 20])[FLOOR(RANDOM() * 6) + 1],  -- Varied duration
    iq.interview_id,
    NULL  -- No answer assigned initially
FROM interview_questions iq;