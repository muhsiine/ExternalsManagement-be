package ma.nttdata.externals.commons.constants;

public final class InterviewPromptConstants {

    private InterviewPromptConstants() {}

    public static final String INTERVIEW_QUESTION_GENERATION_PROMPT = """
            You are an expert interviewing manager and talent acquisition specialist. We need to prepare a list of interview Questions for candidates according to the candidates' profile, Job Offer details, and the provided evaluation types .
            I will give you the information for the candidate's profile, job offer requirements, and evaluation types criteria, generate tailored interview questions based on it.
            Generate exactly {NUMBER_OF_QUESTIONS} interview questions.
            Take these Instructions into consideration:
            - Distribute the {NUMBER_OF_QUESTIONS} questions across all evaluation types based on their coefficient weights. Higher coefficient evaluation types should receive proportionally more questions.
            - Ensure questions align with the candidate's experience level, main technology, and the job requirements.
            - Each question should be relevant to both candidate's background and job requirements.
            - Assign realistic time durations (typically 2-5 minutes per question), taking into account that the duration estimated for the interview is {ESTIMATED_DURATION}.
            - Match question complexity to the candidate's years of experience
            - Reference specific technologies, skills, or experiences mentioned in the candidate profile when relevant.
            - Use simple language: A2-B1-B2
            - Return ONLY a valid JSON array with exactly this structure, no additional text or formatting:"{JSON_SCHEMA}", here you have a mock example:"{JSON_MOCK}".
            Here below I provide the needed information:
            #Candidate Profile: "{CANDIDATE_DATA}",
            #Job Offer requirements: "{OFFER_DATA}",
            #Evaluation Types criteria: "{EVALUATION_TYPES_DATA}".
            """;

    public static final String JSON_SCHEMA = """
            [
              {
                "description": "string - The interview question text",
                "durationInMinutes": "integer - Duration in minutes (2-5)"
              }
            ]
            """;

    public static final String JSON_MOCK = """
            [
              {
                "description": "Given your 8 years of experience with Java and Spring Boot, can you walk me through how you would design a microservices architecture for a high-traffic e-commerce platform? Focus on service decomposition and inter-service communication strategies.",
                "durationInMinutes": 5
              },
              {
                "description": "Describe a challenging technical problem you encountered in one of your previous Spring Boot projects. How did you approach debugging and what was your solution methodology?",
                "durationInMinutes": 4
              },
              {
                "description": "How would you explain the concept of dependency injection in Spring to a junior developer who is new to the framework?",
                "durationInMinutes": 3
              },
              {
                "description": "You need to optimize a Spring Boot application that's experiencing performance issues with database queries. Walk me through your systematic approach to identify and resolve these bottlenecks.",
                "durationInMinutes": 4
              },
              {
                "description": "Tell me about a time when you had to present a complex technical solution to non-technical stakeholders. How did you ensure they understood the implications and benefits?",
                "durationInMinutes": 4
              },
              {
                "description": "Imagine you're tasked with migrating a monolithic application to microservices. What factors would you consider when deciding how to break down the application?",
                "durationInMinutes": 4
              },
              {
                "description": "How do you stay updated with the latest developments in your main technology stack? Can you give me an example of a recent technology or pattern you've learned and applied?",
                "durationInMinutes": 3
              },
              {
                "description": "Describe your experience with version control systems. How do you handle merge conflicts and what branching strategies have you used?",
                "durationInMinutes": 3
              },
              {
                "description": "Walk me through your approach to testing in your projects. What types of tests do you write and how do you ensure good test coverage?",
                "durationInMinutes": 4
              },
              {
                "description": "Tell me about a situation where you had to work under tight deadlines. How did you prioritize tasks and ensure quality delivery?",
                "durationInMinutes": 3
              },
              {
                "description": "How do you approach code reviews? What do you look for when reviewing others' code and how do you handle feedback on your own code?",
                "durationInMinutes": 3
              },
              {
                "description": "Describe a time when you had to learn a new technology quickly for a project. What was your learning strategy?",
                "durationInMinutes": 3
              },
              {
                "description": "How do you ensure the security of the applications you develop? What security practices do you follow?",
                "durationInMinutes": 4
              },
              {
                "description": "Tell me about a challenging team collaboration experience. How did you handle disagreements or conflicts?",
                "durationInMinutes": 3
              },
              {
                "description": "What strategies do you use to debug complex issues in production environments? Can you walk me through a specific example?",
                "durationInMinutes": 4
              }
            ]
            """;

    public static final String INTERVIEW_GENERATE_QUESTIONS_PROMPT_CODE = "INTERVIEW_QUESTION_GENERATION_PROMPT";
    public static final String CANDIDATE_DATA_PLACEHOLDER = "{CANDIDATE_DATA}";
    public static final String OFFER_DATA_PLACEHOLDER = "{OFFER_DATA}";
    public static final String EVALUATION_TYPE_DATA_PLACEHOLDER = "{EVALUATION_TYPES_DATA}";
    public static final String NUMBER_OF_QUESTIONS_PLACEHOLDER = "{NUMBER_OF_QUESTIONS}";
    public static final String ESTIMATED_DURATION_PLACEHOLDER = "{ESTIMATED_DURATION}";
    public static final String JSON_SCHEMA_PLACEHOLDER = "{JSON_SCHEMA}";

}