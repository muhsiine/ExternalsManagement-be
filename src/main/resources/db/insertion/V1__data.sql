-- Insert Country
INSERT INTO country (id, name, english_name)
VALUES (uuid_generate_v4(), 'Maroc', 'Morocco')
ON CONFLICT (english_name) DO NOTHING;

-- Insert Cities
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

-- Insert 40+ Candidates with Moroccan names
INSERT INTO candidates (id, full_name, birth_date, years_of_experience, gender, main_tech, summary)
SELECT
    uuid_generate_v4(),
    first_name || ' ' || last_name,
    DATE '1990-01-01' - (FLOOR(RANDOM() * 365 * 25) || ' days')::INTERVAL,  -- Age 25-50
    FLOOR(RANDOM() * 15) + 1,  -- 1-15 years experience
    gender,
    (ARRAY['Java','Python','JavaScript','Spring Boot','React','Angular','Node.js','PHP','Laravel','Django','Flask','.NET','AWS','Docker','Kubernetes','Android','iOS'])[FLOOR(RANDOM()*17)+1],
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

-- Insert Addresses with fixed array syntax
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
WHERE NOT EXISTS (
    SELECT 1 FROM address a WHERE a.candidate_id = cand.id
);

-- Contacts with Moroccan patterns
INSERT INTO contacts (id, candidate_id, contact_type, contact_value)
SELECT uuid_generate_v4(), id, 'Email', LOWER(SPLIT_PART(full_name, ' ', 1) || '.' || SPLIT_PART(full_name, ' ', 2) || FLOOR(RANDOM()*10)::TEXT || '@domain.ma')
FROM candidates
UNION ALL
SELECT uuid_generate_v4(), id, 'Phone', '+2126' || LPAD(FLOOR(RANDOM()*100000000)::TEXT, 8, '0')
FROM candidates;

-- Experiences with realistic Moroccan companies
INSERT INTO experiences (id, candidate_id, company_name, position, start_date, end_date, description)
SELECT
    uuid_generate_v4(),
    id,
    (ARRAY[
        'OCP Group','Attijariwafa Bank','Maroc Telecom','ONCF','Royal Air Maroc',
        'Saham Assurance','Yazaki Morocco','Managem','SNTL','Cosumar',
        'BMCE Bank','LafargeHolcim','Intelcia','Dell Technologies Morocco',
        'IBM Morocco','Capgemini Morocco','HPS Morocco','Inwi','Orange Morocco'
    ])[FLOOR(RANDOM()*19)+1],
    (ARRAY[
        'Software Engineer','Senior Developer','DevOps Specialist','Tech Lead',
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

-- Skills with varied proficiency
INSERT INTO skills (id, candidate_id, skill_name, proficiency_level)
WITH candidate_skills AS (
    SELECT id, (ARRAY['Java','Spring Boot','React','Angular','Python','Django','Node.js','AWS', 'Docker','Kubernetes','SQL','NoSQL','JavaScript','TypeScript','PHP','Laravel', '.NET','C#','Swift','Kotlin'])[FLOOR(RANDOM()*20)+1] AS skill
    FROM candidates
    CROSS JOIN generate_series(1, (FLOOR(RANDOM()*5)+3)::integer)
)
SELECT uuid_generate_v4(), id, skill, CASE WHEN RANDOM() < 0.2 THEN 'BEGINNER' WHEN RANDOM() < 0.6 THEN 'INTERMEDIATE' ELSE 'EXPERT' END
FROM candidate_skills;

-- Education with Moroccan institutions
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

-- Languages with Moroccan context
INSERT INTO languages (id, candidate_id, description, english_description, full_description, language, language_in_english, level, is_native)
SELECT
    uuid_generate_v4(),
    id,
    lang_data->>'desc',
    lang_data->>'eng_desc',
    lang_data->>'full_desc',
    lang_data->>'lang',
    lang_data->>'eng_lang',
    CASE (RANDOM()*5)::INT
        WHEN 0 THEN 'BEGINNER'
        WHEN 1 THEN 'LOWER_INTERMEDIATE'
        WHEN 2 THEN 'INTERMEDIATE'
        WHEN 3 THEN 'UPPER_INTERMEDIATE'
        ELSE 'ADVANCED'
    END,
    (lang_data->>'native')::BOOLEAN
FROM candidates
CROSS JOIN (
    SELECT jsonb_build_object(
        'desc', 'العربية',
        'eng_desc', 'Arabic',
        'full_desc', 'اللغة العربية الفصحى',
        'lang', 'Arabic',
        'eng_lang', 'Arabic',
        'native', true
    ) AS lang_data
    UNION ALL SELECT jsonb_build_object(
        'desc', 'Français',
        'eng_desc', 'French',
        'full_desc', 'Langue Française',
        'lang', 'French',
        'eng_lang', 'French',
        'native', false
    ) WHERE RANDOM() < 0.8  -- 80% speak French
    UNION ALL SELECT jsonb_build_object(
        'desc', 'English',
        'eng_desc', 'English',
        'full_desc', 'English Language',
        'lang', 'English',
        'eng_lang', 'English',
        'native', false
    ) WHERE RANDOM() < 0.7  -- 70% speak English
    UNION ALL SELECT jsonb_build_object(
        'desc', 'Español',
        'eng_desc', 'Spanish',
        'full_desc', 'Idioma Español',
        'lang', 'Spanish',
        'eng_lang', 'Spanish',
        'native', false
    ) WHERE RANDOM() < 0.2  -- 20% speak Spanish
) AS langs;