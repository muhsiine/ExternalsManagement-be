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
    ),
    dynamic_transcription AS (
SELECT
    t.g,
    -- AI Question (Dynamic Time)
    'AI - 00:' || LPAD((FLOOR(RANDOM() * 20) + 1)::TEXT, 2, '0') || ':00 : ' ||
    (ARRAY[
    'Could you explain the concept of **Polymorphism** in Java and give a real-world example?',
    'Describe a scenario where you would use the **Singleton design pattern** versus a Factory pattern.',
    'What are the performance implications when choosing between **String, StringBuilder, and StringBuffer**?',
    'Walk me through the lifecycle of a **Spring bean** and point out where dependency injection occurs.',
    'Explain how **Garbage Collection** works in the JVM and what Generational Garbage Collection achieves.',
    'How do you manage configuration secrets and credentials in a **Kubernetes** environment?',
    'What is the primary difference between **IaaS, PaaS, and SaaS**, and which do you prefer for application deployment?',
    'Describe your experience with **CI/CD pipelines** using tools like Jenkins or GitLab CI.',
    'When scaling microservices, what strategies do you employ for **service discovery and load balancing**?',
    'What are the advantages of using a **message broker** like Kafka or RabbitMQ in a distributed system?',
    'What is the difference between a **JOIN and an ACID transaction** in PostgreSQL?',
    'Explain the concept of **indexing** in a relational database and how to decide which columns to index.',
    'Describe how you handle large dataset migrations with **zero downtime**.',
    'What are the benefits and drawbacks of using a **NoSQL database** like MongoDB over a traditional SQL database?',
    'How do you optimize slow-running database queries and identify bottlenecks?',
    'Describe a time you had to **mentor a junior developer** on a complex technical issue.',
    'How do you ensure the **security** of the code you write from a high-level perspective?',
    'What is the most complex **legacy code** base you have had to work with, and how did you approach it?',
    'Explain the process of **Test-Driven Development (TDD)** and how it impacts your workflow.',
    'Where do you see yourself in five years, especially concerning **cloud architecture**?'
    ])[FLOOR(RANDOM()*20)+1] || ' || Candidate - 00:' || LPAD((FLOOR(RANDOM() * 20) + 1)::TEXT, 2, '0') || ':30 : ' ||
    -- Candidate Answer (Dynamic Time)
    (ARRAY[
    'Polymorphism allows objects of different classes to be treated as objects of a common type. A good example is using a list of a parent class type to hold various sub-class objects and iterating through them.',
    'The Singleton is best for resources like database connection pools or logging, guaranteeing a single instance. Factory is for abstracting object creation, giving clients flexibility without exposing instantiation logic.',
    'String is immutable, leading to memory overhead with constant concatenation. StringBuilder is faster and mutable but not thread-safe; StringBuffer is thread-safe but slightly slower. I use StringBuilder in single-threaded environments.',
    'The bean lifecycle starts with instantiation, followed by dependency injection (via setters/constructors), initialization callbacks (@PostConstruct), and finally, it’s ready for use. It ends with the container closing and destruction callbacks.',
    'GC tracks and removes unreferenced objects. Generational GC segments the heap into young and old generations, which significantly optimizes performance because most objects die young, avoiding expensive full collections.',
    'I use **Kubernetes Secrets** for sensitive data, coupled with external solutions like HashiCorp Vault or cloud-native key management services for better protection and rotation.',
    'I prefer **PaaS (Platform as a Service)**, like AWS Elastic Beanstalk or Azure App Service, as it balances control with productivity, handling the underlying OS/infrastructure updates for me. IaaS is too much manual overhead.',
    'I have designed Jenkins pipelines that include static code analysis (SonarQube), unit/integration testing, and deployment to staging environments, all triggered automatically on every pull request merge.',
    'I use a **service mesh** like Istio or Consul for automated service discovery, coupled with Round Robin or Least Connections algorithms for traffic distribution at the ingress level.',
    'Message brokers are vital for **decoupling services** and handling asynchronous communication, ensuring reliability for tasks like order fulfillment or notification processing, which don''t require immediate synchronous confirmation.',
    'A **JOIN** is a standard SQL operation to combine columns from two or more rows based on a related column. An **ACID transaction** is a fundamental concept guaranteeing that database operations are reliable and complete as a single unit.',
    'Indexing significantly speeds up search queries on large tables but slows down writes (INSERT/UPDATE). I index columns frequently used in `WHERE` clauses, `ORDER BY`, or `JOIN` conditions, but avoid over-indexing, especially on low-cardinality columns.',
    'I use a **shadow database strategy** or **logical replication**. For shadow databases, the new schema is built alongside the old, and data is double-written during the transition phase, ensuring seamless cutover.',
    'NoSQL is great for **high-velocity, schema-less data** like session logs or user profiles, offering horizontal scalability. SQL is better when data integrity (ACID) and complex relational querying are non-negotiable requirements.',
    'I start by running an `EXPLAIN ANALYZE` on the query to understand its execution plan, looking for **full table scans** or missing indexes. Often, rewriting a subquery or adjusting an index solves the issue.',
    'I use a **pair programming** approach initially, guiding them through debugging without giving them the direct answer. I emphasize the thought process and resource utilization over the immediate fix, building long-term competence.',
    'Security is a layered approach. I start with **OWASP Top 10** checks, use static analysis tools in the pipeline, and ensure all input is validated and sanitized, following the principle of least privilege in access controls.',
    'I dealt with a monolithic banking system using Java 6 and legacy EJBs. My approach was to implement an **anti-corruption layer** around key business domains, allowing us to slowly extract services into a modern framework without halting feature development.',
    'TDD forces me to think about the **contract and interface** before implementation. It results in code that is inherently more testable, modular, and leads to fewer production bugs because the requirements are enshrined in automated tests.',
    'I aim to transition into a **Principal or Lead Architect** role, focusing on designing highly available, multi-region cloud systems, specifically leveraging serverless technologies and advanced networking on AWS or Azure.'
    ])[FLOOR(RANDOM()*20)+1] AS line
FROM generate_series(1, 25) AS t(g)
    )
INSERT INTO interviews (
    id,
    offer_id,
    candidate_id,
    starttime,
    endtime,
    description,
    link,
    scheduled_at,
    comment,
    number_of_questions,
    estimated_duration,
    recording_id,
    transcription -- This column must be in the SELECT list below
)
SELECT
    uuid_generate_v4(),
    o.id,
    c.id,
    t.starttime,
    t.starttime + t.duration AS endtime,
    'Interview for the position',
    'https://meetings.example.com/' || uuid_generate_v4()::TEXT,
            CURRENT_TIMESTAMP + (FLOOR(RANDOM() * 10) || ' days')::INTERVAL,
    'Auto-generated comment for testing',
    15,
    60,
    r.id,
    -- DYNAMICALLY GENERATE THE TRANSCRIPTION STRING
    '[' || string_agg('"' || dt.line || '"', ',' ORDER BY dt.g) || ']' AS transcription
FROM offer_ids o
         CROSS JOIN candidate_ids c
         CROSS JOIN interview_times t
         CROSS JOIN recording_ids r
    -- This lateral join creates a temporary set of random transcription lines for each row
         CROSS JOIN LATERAL (
    SELECT line, g FROM dynamic_transcription
    ORDER BY RANDOM() LIMIT (FLOOR(RANDOM()*20)+1)
    ) dt
-- Group the primary interview columns so we can aggregate the transcription lines
GROUP BY
    o.id,
    c.id,
    t.starttime,
    t.duration,
    r.id
    LIMIT 20;

-- EVALUATION_TYPES (Including OverAll)
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
        WHEN 'OverAll' THEN 4
        ELSE 1
        END
FROM (VALUES
          ('Technical Skills'),
          ('Communication'),
          ('Problem Solving'),
          ('Cultural Fit'),
          ('Experience'),
          ('Motivation'),
          ('OverAll')
     ) AS t(description);

-- EVALUATIONS - Random types per interview + OverAll always included
WITH interview_data AS (
    SELECT id FROM interviews ORDER BY RANDOM() LIMIT 15
    ),
    candidate_profiles AS (
SELECT
    i.id as interview_id,
    CASE (RANDOM() * 4)::INT
    WHEN 0 THEN 'strong'
    WHEN 1 THEN 'average'
    WHEN 2 THEN 'weak'
    ELSE 'mixed'
    END as profile_type
FROM interview_data i
    ),
    interview_evaluations AS (
SELECT DISTINCT
    cp.interview_id,
    cp.profile_type,
    et.id as evaluation_type_id,
    et.description
FROM candidate_profiles cp
    CROSS JOIN evaluation_types et
WHERE
    et.description = 'OverAll'  -- Always include OverAll
   OR
    (et.description != 'OverAll' AND RANDOM() < 0.4)  -- 40% chance for other types
    )
INSERT INTO evaluations (id, score, feedback, interview_id, evaluation_type_id)
SELECT
    uuid_generate_v4(),
    CASE
        WHEN ie.profile_type = 'strong' THEN 80 + (RANDOM() * 20)::INT
        WHEN ie.profile_type = 'average' THEN 60 + (RANDOM() * 25)::INT
        WHEN ie.profile_type = 'weak' THEN 30 + (RANDOM() * 35)::INT
        ELSE 50 + (RANDOM() * 40)::INT
END,
    CASE
        WHEN ie.profile_type = 'strong' THEN
            (ARRAY[
                'Excellent performance with strong competencies demonstrated. The candidate displayed a high level of expertise in their domain, with a clear and confident approach to complex challenges.',
                'Outstanding results across all evaluation criteria. Their work ethic and proficiency were exceptional, exceeding all expectations for the role.',
                'Exceptional candidate with impressive skills and experience. The candidate`s background perfectly aligns with our needs, showing great potential for immediate contribution and leadership.',
                'Superior performance with great potential for success. The candidate has a unique set of skills and a keen insight that would be a significant asset to any team.'
            ])[FLOOR(RANDOM() * 4) + 1]
        WHEN ie.profile_type = 'average' THEN
            (ARRAY[
                'Good performance with a solid foundation and growth potential. The candidate has a decent understanding of the core concepts but could benefit from further development in specific areas.',
                'Adequate skills demonstrated with room for development. The candidate meets the basic requirements of the role, but their skills may require some refinement to meet our long-term goals.',
                'Reasonable competency shown with some areas for improvement. While the candidate is proficient in certain tasks, there are gaps in their knowledge that would need to be addressed through training.',
                'Fair performance with potential for success with support. The candidate has a positive attitude and a willingness to learn, but they would need mentorship and guidance to reach their full potential.'
            ])[FLOOR(RANDOM() * 4) + 1]
        WHEN ie.profile_type = 'weak' THEN
            (ARRAY[
                'Below expectations with significant areas needing improvement. The candidate struggled with fundamental concepts and lacked the necessary skills to perform the required duties effectively.',
                'Weak performance with major gaps in required competencies. The candidate`s background and experience do not align well with the demands of the role, leading to significant concerns about their suitability.',
                'Insufficient demonstration of skills needed for the role. The candidate failed to provide convincing examples of their capabilities and seemed unprepared for the technical challenges of the interview.',
                'Poor results with concerns about role suitability. The candidate lacks the foundational knowledge and practical experience necessary for this position, making them a poor fit for the role.'
            ])[FLOOR(RANDOM() * 4) + 1]
        ELSE
            (ARRAY[
                'Mixed performance with strengths in some areas but gaps in others. The candidate excelled in a few tasks but struggled with others, leading to an inconsistent overall assessment.',
                'Variable results showing potential but inconsistent execution. The candidate showed flashes of brilliance, but their performance was unpredictable, raising concerns about reliability.',
                'Uneven demonstration with good qualities offset by weaknesses. While the candidate has some positive attributes, their limitations in critical areas make them a risky hire.'
            ])[FLOOR(RANDOM() * 3) + 1]
END,
    ie.interview_id,
    ie.evaluation_type_id
FROM interview_evaluations ie;

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
