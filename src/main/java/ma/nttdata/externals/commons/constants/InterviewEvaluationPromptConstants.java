package ma.nttdata.externals.commons.constants;

public final class InterviewEvaluationPromptConstants {

    private InterviewEvaluationPromptConstants(){}

    public static final String JSON_MOCK = """
        [
          {
             "score": 78,
             "feedback": "The candidate demonstrated solid problem-solving skills by breaking down complex scenarios into manageable steps. They showed logical thinking when approaching the algorithm challenge, though they took 35% longer than estimated time (12 minutes vs 8 minutes expected). Their approach was methodical but could benefit from faster pattern recognition to improve efficiency in time-constrained situations.",
             "evaluationTypeDescription": "Problem Solving"
           },
           {
             "score": 82,
             "feedback": "Communication was clear and well-structured throughout the interview. The candidate articulated technical concepts effectively and asked relevant clarifying questions. They maintained good eye contact and professional demeanor. Response time was generally within expected ranges. Minor improvement area: occasionally used too much technical jargon when simpler explanations would suffice for broader team communication.",
             "evaluationTypeDescription": "Communication"
           },
           {
             "score": 88,
             "feedback": "The candidate demonstrated strong technical expertise, accurately answering most technical questions and applying relevant frameworks and tools to proposed solutions. They showcased up-to-date knowledge in backend development and database optimization. A slight gap was noted in the depth of cloud deployment strategies.",
             "evaluationTypeDescription": "Technical Knowledge"
           },
           {
             "score": 74,
             "feedback": "Teamwork skills appear solid, with examples of successful collaboration and conflict resolution in previous roles. The candidate emphasizes open communication and adaptability when working with different personalities. Could improve by actively seeking feedback from peers during collaborative tasks.",
             "evaluationTypeDescription": "Teamwork"
           },
           {
             "score": 91,
             "feedback": "The candidate showed high adaptability when presented with unexpected problem changes. They adjusted their approach quickly and kept focus on delivering a workable solution without losing quality. This flexibility is valuable in dynamic project environments.",
             "evaluationTypeDescription": "Adaptability"
           },
           {
             "score": 69,
             "feedback": "Time management was acceptable but could be optimized. While the candidate met deadlines for most answers, they occasionally exceeded the suggested time limit in more complex questions, which may affect performance under strict delivery timelines.",
             "evaluationTypeDescription": "Time Management"
           }
        ]
        """;


    public static final String OFFER_PLACEHOLDER = "{OFFER_DATA}";
    public static final String CANDIDATE_PLACEHOLDER = "{CANDIDATE_DATA}";
    public static final String QUESTION_ANSWER_DTO_PLACEHOLDER = "{QuestionAnswer_DATA}";
    public static final String EVALUATION_TYPES_PLACEHOLDER = "{EVALUATION_TYPES_DATA}";
    public static final String JSON_SCHEMA_PLACEHOLDER = "{JSON_SCHEMA}";
    public static final String JSON_MOCK_PLACEHOLDER = "{JSON_MOCK}";
}