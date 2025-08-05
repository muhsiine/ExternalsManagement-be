package ma.nttdata.externals.commons.constants;

public final class InterviewEvaluationPromptConstants {

    private InterviewEvaluationPromptConstants(){}

    public static final String INTERVIEW_EVALUATION_PROMPT = """
            You are an evaluation expert. We need you to prepare a list of evaluations for each evaluation type based on Questions and Answers of the candidate.
            
            Your evaluation must be comprehensive and fair by considering ALL the provided information:
            
            1. **Time Management Analysis**: Compare the estimated answer time vs real answer time for each question. Factor in the total interview duration ({InterviewDuration}) and number of questions ({NumberOfQuestions}) to assess if time was managed effectively throughout the interview.
            
            2. **Candidate-Job Alignment**: Evaluate answers against the specific job requirements in {OfferData} while considering the candidate's background from {CandidateData}. Adjust expectations based on the candidate's experience level, education, and previous roles.
            
            3. **Evaluation Type Weighting**: Each evaluation type in {EvaluationTypes} has a coefficient - ensure your scores reflect the relative importance of each evaluation type. Higher coefficient types should have more rigorous assessment criteria.
            
            4. **Answer Quality Assessment**: Analyze each answer in {QuestionAnswerDTO} for technical accuracy, depth of knowledge, problem-solving approach, and relevance to the evaluation type being assessed.
            
            When evaluating the candidate please follow these instructions:
            - Cross-reference candidate answers with job requirements to ensure role-specific evaluation
            - Calculate time efficiency: significant deviations (>30% over/under estimated time) should impact scores
            - Align scoring with evaluation type coefficients - critical evaluation types demand higher standards
            - Consider interview context: {NumberOfQuestions} questions in {InterviewDuration} minutes affects expected answer depth
            - Provide specific feedback referencing actual answers and time performance
            - Ensure fairness by matching evaluation difficulty to candidate's stated experience level.
            - Return a valid Json Array, and respect this structure do not add anything to it "{JSON_SCHEMA}".
            - Maintain the order of evaluations in the output exactly as provided in the 'EvaluationTypes' list.
            
            Here is the information needed for the evaluation:
            #Interview Duration: "{InterviewDuration}",
            #Number of Questions: "{NumberOfQuestions}",
            #Question And Answers with their estimated answer time and the real answer time: "{QuestionAnswerDTO}",
            #Evaluation Types: "{EvaluationTypes}",
            #Candidate Profile: "{CandidateData}",
            #Offer Data: "{OfferData}",
            """;

    public static final String JS0N_SCHEMA = """
            [
              {
                "score": "Double - between 0.00 and 100.00",
                "feedback": "String - Try to give a an overall feedback of the performance of the candidate in this evaluation type",
                "evaluationType": "String - description of the evaluationType"
              }
            ]
            """;

    public static final String JSON_MOCK = """
            [
              {
                 "score": 78,
                 "feedback": "The candidate demonstrated solid problem-solving skills by breaking down complex scenarios into manageable steps. They showed logical thinking when approaching the algorithm challenge, though they took 35% longer than estimated time (12 minutes vs 8 minutes expected). Their approach was methodical but could benefit from faster pattern recognition to improve efficiency in time-constrained situations.",
                 "evaluationType": "Problem Solving"
               },
               {
                 "score": 82,
                 "feedback": "Communication was clear and well-structured throughout the interview. The candidate articulated technical concepts effectively and asked relevant clarifying questions. They maintained good eye contact and professional demeanor. Response time was generally within expected ranges. Minor improvement area: occasionally used too much technical jargon when simpler explanations would suffice for broader team communication.",
                 "evaluationType": "Communication"
               },
               {
                 "score": 85,
                 "feedback": "Strong React knowledge demonstrated through detailed explanations of hooks, state management, and component lifecycle. The candidate provided specific examples from their previous projects and correctly explained concepts like useEffect dependencies and context API. Answered React questions 10% faster than estimated, showing confidence in the technology. Well-aligned with the senior React developer requirements in the job offer.",
                 "evaluationType": "React"
               },
               {
                 "score": 73,
                 "feedback": "Good foundational knowledge of Node.js concepts including event loop, middleware, and API development. However, struggled with some advanced topics like clustering and performance optimization, taking 45% longer than expected on these questions. Given the candidate's 3 years of experience mentioned in their profile, this performance is adequate but shows room for growth in enterprise-level Node.js applications.",
                 "evaluationType": "Node.js"
               },
               {
                 "score": 79,
                 "feedback": "Overall solid performance across all evaluation areas. The candidate completed 8 questions in the 60-minute interview timeframe, showing reasonable time management. Their technical skills align well with the full-stack developer position requirements, though some areas need strengthening. Communication style fits the collaborative team environment described in the job offer. Recommended for next round with focus on Node.js advanced concepts.",
                 "evaluationType": "Overall Evaluation"
               }
            ]
            """;

    public static final String OFFER_PLACEHOLDER = "{OfferData}";
    public static final String CANDIDATE_PLACEHOLDER = "{CandidateData}";
    public static final String QUESTION_ANSWER_DTO_PLACEHOLDER = "{QuestionAnswerDTO}";
    public static final String EVALUATION_TYPES_PLACEHOLDER = "{EvaluationTypes}";
    public static final String INTERVIEW_DURATION_PLACEHOLDER = "{InterviewDuration}";
    public static final String NUMBER_OF_QUESTIONS_PLACEHOLDER = "{NumberOfQuestions}";
    public static final String JSON_SCHEMA_PLACEHOLDER = "{JSON_SCHEMA}";
}
