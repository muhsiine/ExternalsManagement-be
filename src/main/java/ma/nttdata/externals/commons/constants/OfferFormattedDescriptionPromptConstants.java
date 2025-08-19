package ma.nttdata.externals.commons.constants;

public final class OfferFormattedDescriptionPromptConstants {

    private OfferFormattedDescriptionPromptConstants() {}

    public static final String OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT = """
            You are an expert HR data extraction specialist.
             I will give you an #Offer_Description, Extract the key information from it, and return a structured, formatted Json like provided in #JSON_SCHEMA, when extracting information, respect the schema we want an exact match, we'll provide also a and #JSON_MOCK to help with that.
            Take these instructions into consideration:
            -For all the fields, keep the original text from the provided description just extract from it what matches the criteria.
            -description: The main offer description and company information (keep the original text from the provided description just extract from it).
            - skills: The required technical and professional skills.
             - languages: The required language names and the proficiency levels. the names should be in English, the proficiency level should be one of these words: [BEGINNER, LOWER_INTERMEDIATE ,INTERMEDIATE, UPPER_INTERMEDIATE, ADVANCED], it might not be present in the description with the exact name, detect the required language level, and convert it accordignly to one of these values.
             - yearsOfExperience: The required years of experience in the offer description (from "3-5 years" extract 3, from "5+ years" extract 5).
             - mainResponsibilities: The tasks and duties mentioned in the offer description, that the candidate will perform if he got accepted in the offer.
             - education: The required education from the offer description.
             - keywords: The keywords of the offer description.
             Return only a valid JSON object with exactly this structure, no additional text or formatting,  #JSON_SCHEMA structure: "{JSON_SCHEMA}"
             Here is an example of the expected output, #JSON_MOCK structure: "{JSON_MOCK}"
             #Offer_Description: "{OFFER_DESCRIPTION}
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
          "mainTech": "Full Stack Javascript",
          "skills": "Java - Spring Boot - Docker - Kubernetes - AWS - MySQL - Git - Agile - Team Leadership - Communication",
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
