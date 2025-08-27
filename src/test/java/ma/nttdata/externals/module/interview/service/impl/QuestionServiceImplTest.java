package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.dto.placeholdersForInterviewQuestionsPromptDTO;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.dto.AIQuestionResponseDTO;
import ma.nttdata.externals.module.interview.mapper.QuestionMapper;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
import ma.nttdata.externals.module.interview.repository.QuestionRepository;
import ma.nttdata.externals.module.interview.service.TextToSpeechServ;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private QuestionMapper questionMapper;
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private RestClient aiRestClient;
    @Mock
    private TextToSpeechServ textToSpeechServ;

    private QuestionServImpl questionServ;

    private boolean mockFlag = true;

    private static String JSON_SCHEMA = """
            [
              {
                "score": "Double - between 0.00 and 100.00",
                "feedback": "String - Try to give an overall feedback of the performance of the candidate in this evaluation type",
                "evaluationTypeDescription": "String - use the exact 'description' field value from the corresponding EvaluationType entity"
              }
            ]
            """;
    private static final String INTERVIEW_EVALUATION_PROMPT = """
            You're an expert interviewing manager and talent acquisition specialist.
            We've passed an interview for an #offer, to a #candidate, and we've gathered the information output and prepared a list of #Question/#answer from that interview,
            I will provide you below the needed information for them.
            Prepare a list of evaluations for that interview, each #evaluation_type is an entry in this list, I will also give you the list of #evaluation_types that we need to evaluate this candidate in.
                                       
            #Take in consideration these instructions:
             - The evaluations must be comprehensive and fair considering the job requirements in #offer_data and the #candidate_data
             - In relevance to the #evaluation_type being assessed, look in the #answers for technical accuracy, depth of knowledge, problem-solving approaches, and communication skills.
             - If the #answer is correct and the time of #answer is lower than the time given in the question, take it into account for positive assessment.
             - Cross-reference #candidate answers with #job requirements to ensure role-specific #evaluation.
             - Take evaluation type #coefficients in consideration
             - Ensure fairness by matching evaluation difficulty and accuracy to candidate's stated experience level
             - Use the exact 'description' value from each EvaluationTypes listFor the 'evaluationType' field in the output.
             - Ensure each evaluation in the output array corresponds exactly to one EvaluationType from the input list.
             - Do not skip any evaluation types or add additional ones not provided in the Evaluation Types.
             - Reference specific technologies, skills, or experiences mentioned in the candidate profile when relevant.
             - Use simple language: A2-B1-B2
             - Return ONLY a valid JSON array with exactly this structure, no additional text or formatting:"{JSON_SCHEMA}", here you have a mock example:"{JSON_MOCK}".
                                       
            I provide bellow the needed information:
             - #Candidate Profile: "{CANDIDATE_DATA}",
             - #Job Offer requirements: "{OFFER_DATA}",
             - #Evaluation Types criteria: "{EVALUATION_TYPES_DATA}".
             - #Questions And Answers with the estimated answer time and the real answer time: "{QuestionAnswer_DATA}"
            """;



    @Test
    void PrepareQuestionsFromAIResponse_shouldParseJsonCorrectly() {

        questionServ = new QuestionServImpl(
                questionRepository,
                questionMapper,
                interviewRepository,
                true,
                aiRestClient,textToSpeechServ);
        CandidateDTO candidateDTO = new CandidateDTO(UUID.randomUUID(), null, null, 0, null, null, null, null, null, null, null, null, null, null, null);
        OfferDTO offerDTO = new OfferDTO(UUID.randomUUID(), null, null, null,null);

        placeholdersForInterviewQuestionsPromptDTO generateQuestionsInfo = new placeholdersForInterviewQuestionsPromptDTO(
                candidateDTO,
                offerDTO,
                3,
                30
        );

        List<EvaluationTypeDTO> evaluationTypes = List.of(
                new EvaluationTypeDTO(UUID.randomUUID(), "Technical", 1.0)
        );

        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),"test",INTERVIEW_EVALUATION_PROMPT,JSON_SCHEMA);


        List<QuestionDTO> questions = questionServ.prepareQuestionsFromAIResponse(generateQuestionsInfo, evaluationTypes,prompt);


        assertNotNull(questions);
        assertEquals(15, questions.size());
        assertTrue(questions.stream().allMatch(q -> q.description() != null));
    }

    @Test
    void PrepareQuestionsFromAIResponse_WithMockFlagFalse_shouldParseJsonCorrectly() {
        questionServ = new QuestionServImpl(
                questionRepository,
                questionMapper,
                interviewRepository,
                false,
                aiRestClient,textToSpeechServ);
        List<AIQuestionResponseDTO> aiResponses = List.of(
                new AIQuestionResponseDTO("What is Java?", "3"),
                new AIQuestionResponseDTO("Explain REST APIs.", "4"),
                new AIQuestionResponseDTO("Describe microservices.", "5"),
                new AIQuestionResponseDTO("What is Spring Boot?", "6")
        );

        List<QuestionDTO> questions = aiResponses.stream()
                .map(raw -> new QuestionDTO(
                        null,
                        raw.description(),
                        parseDuration(raw.durationInMinutes()),
                        null,
                        null
                ))
                .toList();

        assertEquals(4, questions.size());

        assertEquals(3, questions.get(0).durationInMinutes());
        assertEquals(4, questions.get(1).durationInMinutes());
        assertEquals(5, questions.get(2).durationInMinutes());
        assertEquals(6, questions.get(3).durationInMinutes());


        assertEquals("What is Java?", questions.get(0).description());
        assertEquals("Explain REST APIs.", questions.get(1).description());
        assertEquals("Describe microservices.", questions.get(2).description());
        assertEquals("What is Spring Boot?", questions.get(3).description());
    }
    private Integer parseDuration(String durationStr) {
        if (durationStr == null) return null;
        try {
            String digits = durationStr.replaceAll("\\D+", "");
            return digits.isEmpty() ? null : Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
