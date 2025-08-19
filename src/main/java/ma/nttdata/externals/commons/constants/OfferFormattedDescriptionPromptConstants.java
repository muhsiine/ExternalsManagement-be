package ma.nttdata.externals.commons.constants;

public final class OfferFormattedDescriptionPromptConstants {

    private OfferFormattedDescriptionPromptConstants() {}

    public static final String OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT = """
        You are an expert HR data extraction specialist. Your task is to extract key information from job offer descriptions to create structured, formatted data for frontend display in our interview system.
        I will give you the the information for the #offerDescription, #JSON_MOCK an example of the data we want and #JSON_SCHEMA you should respect when extracting information.                                 
        Take these instructions into consideration:
        - description: The main job description and company information (keep the original descriptive content for display)
        - mainTech: The primary technology stack or focus of the job, formatted like skills (format: "Tech,Level"), e.g., "Java,ADVANCED"
        - skills: Technical and professional skills required with proficiency levels (format: "Skill,Level - Skill,Level"), e.g., "Java,ADVANCED - Spring Boot,INTERMEDIATE - Docker,BEGINNER"
        - languages: Required languages with proficiency levels converted to enum values using mapping: A1, A2, Basic, Elementary → BEGINNER; B1, Lower Intermediate → LOWER_INTERMEDIATE; B2, Intermediate → INTERMEDIATE; C1, Upper Intermediate → UPPER_INTERMEDIATE; C2, Advanced, Fluent, Native → ADVANCED, and for the names of languages they should be in english.
        - yearsOfExperience: Minimum years of experience (from "3-5 years" extract 3, from "5+ years" extract 5)
        - mainResponsibilities: Core day-to-day tasks and duties the candidate will perform, separated by " - "
        - education: Required degree level and field of study, separated by " - "
        - keywords: Key terms from job title, critical skills, and industry-specific terminology that don't fit in other categories

        Return ONLY a valid JSON object with exactly this structure, no additional text or formatting: "{JSON_SCHEMA}"

        Here is an example of the expected output: "{JSON_MOCK}"

        Job Offer Description to extract from: "{OFFER_DESCRIPTION}"
        """;

    public static final String JSON_SCHEMA = """
        {
          "description": "string - The job description text",
          "mainTech": "string - Primary technology stack or focus, e.g., 'Full stack Java SpringBoot'",
          "skills": "string - Offer required skills separated by ' - ' e.g., 'Java - Spring Boot - Docker'",
          "languages": "array of objects - [{'languageName': 'English', 'level': 'ADVANCED'}]",
          "yearsOfExperience": "integer - Minimum required years of experience",
          "mainResponsibilities": "string - offer tasks separated by ' - '",
          "education": "string - Education requirements",
          "keywords": "string - Offer Keywords separated by ' - '"
        }
        """;

    public static final String JSON_MOCK = """
        {
          "description": "We are seeking a highly skilled Senior Java Developer to join our dynamic fintech team. The role involves designing and implementing scalable microservices, collaborating with cross-functional teams, and ensuring high-quality code standards. The candidate will contribute to architecture decisions, mentor junior developers, and help drive the adoption of best practices.",
          "mainTech": "Java,ADVANCED",
          "skills": "Java,ADVANCED - Spring Boot,ADVANCED - Docker,INTERMEDIATE - Kubernetes,INTERMEDIATE - AWS,INTERMEDIATE - MySQL,ADVANCED - Git,ADVANCED - Agile,INTERMEDIATE - Team Leadership,ADVANCED - Communication,ADVANCED",
          "languages": [
            {"languageName": "English", "level": "ADVANCED"},
            {"languageName": "French", "level": "INTERMEDIATE"}
          ],
          "yearsOfExperience": 6,
          "mainResponsibilities": "Design and implement microservices architecture - Optimize application performance - Maintain CI/CD pipelines - Conduct code reviews and mentor junior developers - Collaborate with product managers and QA team - Participate in on-call rotation",
          "education": "Bachelor in Computer Science - Master in Software Engineering",
          "keywords": "Java - Spring Boot - Microservices - Docker - Kubernetes - Fintech - Agile - DevOps - AWS - Backend Development"
        }
        """;

    public static final String OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT_CODE = "OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT";
    public static final String OFFER_DESCRIPTION_PLACEHOLDER = "{OFFER_DESCRIPTION}";
    public static final String JSON_SCHEMA_PLACEHOLDER = "{JSON_SCHEMA}";
    public static final String JSON_MOCK_PLACEHOLDER = "{JSON_MOCK}";

}
