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
       (ARRAY[
            'bouraouiyoussef12@gmail.com',
        'mohamedtaha.elyakoubi@nttdata.com'
            ])[FLOOR(RANDOM()*2 + 1)]
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
     'IBM Morocco','Capgemini Morocco','HPS Morocco','Inwi','Orange Morocco'
         ])[FLOOR(RANDOM()*19)+1],
    (ARRAY['Software Engineer','Senior Developer','DevOps Specialist','Tech Lead',
           'Full Stack Developer','Data Engineer','Mobile Developer','Cloud Architect'
    ])[FLOOR(RANDOM()*8)+1],
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

-- 🔧 languages (force English + random others)
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
            -- 🔧 Reduced pool, aligned with candidates
            'mainTech', (ARRAY['Java','Python','React','Angular'])[FLOOR(RANDOM()*4)+1],
            'skills', (ARRAY[
                'Java - Spring Boot - Docker - Kubernetes - AWS',
                'React - Node.js - MongoDB - Docker - Git',
                'Python - Django - PostgresSQL - Kubernetes - AWS',
                'C# - .NET - SQL Server - Azure - Agile'
            ])[FLOOR(RANDOM() * 4) + 1],
            -- 🔧 Always English + maybe French/Spanish
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

-- INTERVIEWS
WITH offer_ids AS (
    SELECT id FROM offers ORDER BY RANDOM() LIMIT 10
    ),
    candidate_ids AS (
SELECT id FROM candidates ORDER BY RANDOM() LIMIT 20
    ),
    interview_times AS (
SELECT
    CURRENT_TIMESTAMP - (FLOOR(RANDOM() * 30) || ' days')::INTERVAL AS starttime,
    (30 + FLOOR(RANDOM() * 60)) * INTERVAL '1 minute' AS duration
    ),
    recording_ids AS (
SELECT id FROM recording ORDER BY RANDOM() LIMIT 20
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
    o.id,
    c.id,
    t.starttime,
    t.starttime + t.duration AS endtime,
    'Interview for the position',
    'https://meetings.example.com/' || uuid_generate_v4()::TEXT,
    NULL,
    CURRENT_TIMESTAMP + (FLOOR(RANDOM() * 10) || ' days')::INTERVAL,
    'Auto-generated comment for testing',
    15,
    60,
    r.id
FROM offer_ids o
         CROSS JOIN candidate_ids c
         CROSS JOIN interview_times t
         CROSS JOIN recording_ids r
    LIMIT 20;

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
WITH interview_ids AS (
    SELECT id FROM interviews ORDER BY RANDOM() LIMIT 20
    ),
    evaluation_type_ids AS (
SELECT id FROM evaluation_types ORDER BY RANDOM() LIMIT 3
    )
INSERT INTO evaluations (id, score, feedback, interview_id, evaluation_type_id)
SELECT
    uuid_generate_v4(),
    ROUND((RANDOM() * 100)::NUMERIC, 2),
    (ARRAY[
         'Excellent performance',
     'Good knowledge but lacks experience',
     'Strong communication skills',
     'Needs improvement in problem solving',
     'Great cultural fit',
     'Highly motivated and eager to learn',
     'Average technical skills'
         ])[FLOOR(RANDOM() * 7) + 1],
    i.id,
    e.id
FROM interview_ids i
    CROSS JOIN evaluation_type_ids e
    LIMIT 40;

-- ANSWERS and QUESTIONS seeding together with FK links fixed
WITH inserted_answers AS (
INSERT INTO answers (id, description, duration_in_minutes)
SELECT
    uuid_generate_v4(),
    (ARRAY[
         'I worked on a large-scale system with a distributed architecture.',
     'I break down tasks and communicate constantly with stakeholders.',
     'I debugged a memory leak issue that improved performance by 30%.',
     'I am passionate about learning and applying new skills.',
     'I follow tech blogs, attend webinars, and take courses.',
     'I believe teamwork and clear communication are key.',
     'I use task management tools and set clear priorities daily.'
         ])[FLOOR(RANDOM() * 7) + 1],
    (ARRAY[2, 3, 4, 5])[FLOOR(RANDOM() * 4) + 1]
FROM generate_series(1, 50)
    RETURNING id
    ),
    random_interviews AS (
SELECT id FROM interviews ORDER BY RANDOM() LIMIT 50
    )
INSERT INTO questions (id, description, duration_in_minutes, interview_id, answer_id)
SELECT
    uuid_generate_v4(),
    (ARRAY[
         'Explain your previous project experience.',
     'How do you handle tight deadlines?',
     'Describe a difficult technical problem you solved.',
     'What motivates you to work in tech?',
     'How do you stay updated with new technologies?',
     'Describe your experience working in a team.',
     'How do you prioritize tasks during a project?'
         ])[FLOOR(RANDOM() * 7) + 1],
  (ARRAY[5, 10, 15, 20])[FLOOR(RANDOM() * 4) + 1],
  ri.id,
  ia.id
FROM random_interviews ri
    JOIN inserted_answers ia ON TRUE
    LIMIT 50;
