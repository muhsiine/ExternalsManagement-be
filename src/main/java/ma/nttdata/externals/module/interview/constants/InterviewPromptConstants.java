package ma.nttdata.externals.module.interview.constants;

public class InterviewPromptConstants {
    public static final String PROMPT_TEXT = 
        "I will provide you with structured data extracted from a Hibernate-based candidate profile database. " +
        "Based on this information, I would like you to generate 10 interview questions of {level} difficulty, " +
        "focused on the technology {main_tech}. These questions should reflect the candidate's experience, " +
        "educational background, and technical skills. Below is the relevant candidate data in JSON format: {json_data}. " +
        "Please make sure the questions are well-balanced in terms of theory, practical application, " +
        "and real-world scenarios, tailored to the candidate's experience level and technology focus.";

    public static final String JSON_SCHEMA = """
        {
            "type": "object",
            "properties": {
                "full_name": { "type": "string" },
                "years_of_experience": { "type": "integer" },
                "main_tech": { "type": "string" },
                "skills": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "skill_name": { "type": "string" },
                            "proficiency_level": { "type": "string" }
                        }
                    }
                },
                "experiences": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "company_name": { "type": "string" },
                            "position": { "type": "string" },
                            "duration": { "type": "string" }
                        }
                    }
                },
                "educations": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "degree": { "type": "string" },
                            "field": { "type": "string" },
                            "institution": { "type": "string" }
                        }
                    }
                },
                "languages": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "language": { "type": "string" },
                            "level": { "type": "string" }
                        }
                    }
                }
            }
        }
        """;
} 