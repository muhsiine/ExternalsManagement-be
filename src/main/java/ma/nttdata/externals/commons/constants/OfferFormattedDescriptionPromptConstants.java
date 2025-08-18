package ma.nttdata.externals.commons.constants;

public final class OfferFormattedDescriptionPromptConstants {

    private OfferFormattedDescriptionPromptConstants() {}

        public static final String OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT = """
            You are an expert HR data extraction specialist. Your task is to extract key information from job offer descriptions to create structured candidate profiles for our interview system.
            
            Extract the following information from the provided job offer description:
            
            FIELDS TO EXTRACT:
            - about: Extract ONLY essential requirements (nationality, visa status, security clearance, location constraints, travel requirements). Ignore generic company descriptions and marketing content.
            - skills: Technical and professional skills required with proficiency levels (programming languages, frameworks, tools, certifications, soft skills)
            - languages: Required languages with proficiency levels (native, fluent, conversational, basic)
            - yearsOfExperience: Minimum and/or preferred years of experience (handle ranges like "3-5 years" or "5+ years")
            - mainResponsibilities: Core day-to-day tasks and duties the candidate will perform (focus on actionable responsibilities, not job titles)
            - education: Required degree level and field of study (both are important for matching)
            - keywords: Key terms from job title, critical skills, and industry-specific terminology that don't fit in other categories
            
            EXTRACTION GUIDELINES:
            - Be concise and precise in your extractions
            - For skills: Include proficiency levels when specified (expert, advanced, intermediate, basic) or requirements level (required, preferred)
            - For education: Capture both level (Bachelor's, Master's, etc.) and field (Computer Science, Engineering, etc.)
            - For experience: Extract both minimum requirements and preferred levels if mentioned
            - For responsibilities: Focus on what the person will actually do, not company goals
            - For about: Only include information relevant to candidate eligibility and requirements
            - For keywords: Include terms that are crucial for candidate matching but don't fit other categories
            
            Return ONLY a valid JSON object with exactly this structure, no additional text or formatting: "{JSON_SCHEMA}"
            
            Here is an example of the expected output: "{JSON_MOCK}"
            
            Job Offer Description to extract from: "{OFFER_DESCRIPTION}"
            """;

        public static final String JSON_SCHEMA = """
            {
              "about": "string - Essential requirements only (nationality, visa, location, clearance, travel)",
              "skills": "array of objects - [{'skill': 'Java', 'level': 'advanced', 'required': true}]",
              "languages": "array of objects - [{'language': 'English', 'level': 'fluent'}]",
              "yearsOfExperience": "string - Experience requirements (e.g., '3-5 years', '5+ years')",
              "mainResponsibilities": "array of strings - Core tasks and duties",
              "education": "array of objects - [{'level': 'Bachelor's', 'field': 'Computer Science', 'required': true}]",
              "keywords": "array of strings - Key terms for matching not covered in other fields"
            }
            """;

        public static final String JSON_MOCK = """
            {
              "about": "EU citizenship required, willing to travel 25% of the time, security clearance preferred",
              "skills": [
                {
                  "skill": "Java",
                  "level": "advanced",
                  "required": true
                },
                {
                  "skill": "Spring Boot",
                  "level": "intermediate",
                  "required": true
                },
                {
                  "skill": "Microservices",
                  "level": "advanced",
                  "required": true
                },
                {
                  "skill": "Docker",
                  "level": "intermediate",
                  "required": false
                },
                {
                  "skill": "Kubernetes",
                  "level": "basic",
                  "required": false
                },
                {
                  "skill": "AWS",
                  "level": "intermediate",
                  "required": true
                },
                {
                  "skill": "MySQL",
                  "level": "intermediate",
                  "required": true
                },
                {
                  "skill": "Team leadership",
                  "level": "advanced",
                  "required": true
                },
                {
                  "skill": "Problem solving",
                  "level": "advanced",
                  "required": true
                },
                {
                  "skill": "Communication",
                  "level": "advanced",
                  "required": true
                }
              ],
              "languages": [
                {
                  "language": "English",
                  "level": "fluent"
                },
                {
                  "language": "French",
                  "level": "conversational"
                }
              ],
              "yearsOfExperience": "5+ years in backend development, 2+ years in team leadership",
              "mainResponsibilities": [
                "Design and develop microservices architecture",
                "Lead a team of 4-6 developers",
                "Conduct code reviews and technical discussions",
                "Collaborate with product managers on requirements",
                "Mentor junior developers",
                "Optimize application performance and scalability",
                "Implement CI/CD pipelines",
                "Participate in on-call rotation"
              ],
              "education": [
              {
                "level": "Bachelor's",
                "field": "Computer Science or Engineering",
                "required": true
              },
              {
                "level": "Master's",
                "field": "Computer Science",
                "required": true
              }
              ],
              "keywords": [
                "Senior Backend Developer",
                "Team Lead",
                "Fintech",
                "Agile",
                "Scrum Master",
                "API Design",
                "DevOps"
              ]
            }
            """;

        public static final String OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT_CODE = "OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT";
        public static final String OFFER_DESCRIPTION_PLACEHOLDER = "{OFFER_DESCRIPTION}";
        public static final String JSON_SCHEMA_PLACEHOLDER = "{JSON_SCHEMA}";
        public static final String JSON_MOCK_PLACEHOLDER = "{JSON_MOCK}";


}
