package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.commons.constants.InterviewEvaluationPromptConstants;
import ma.nttdata.externals.commons.constants.InterviewPromptConstants;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.interview.mapper.EvaluationMapper;
import ma.nttdata.externals.module.interview.repository.EvaluationRepository;
import ma.nttdata.externals.module.interview.repository.EvaluationTypeRepository;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EvaluationServImplTest {


    @Mock
    private  EvaluationRepository evaluationRepository;
    @Mock
    private EvaluationMapper evaluationMapper;
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private EvaluationTypeRepository evaluationTypeRepository;
    @Mock
    private RestClient aiRestClient;

    private EvaluationServImpl evaluationServ;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

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
    void prepareEvaluationResponseFromAi_shouldReturnParsedList_whenMockFlagFalse() throws Exception {
        evaluationServ = new EvaluationServImpl(
                evaluationRepository,
                evaluationMapper,
                interviewRepository,
                evaluationTypeRepository,
                false,
                aiRestClient
        );

        EvaluationServImpl spyService = spy(evaluationServ);

        String mockedJson = """
            [
                {"score": 95, "feedback": "Excellent technical depth", "evaluationTypeDescription": "Backend"},
                {"score": 88, "feedback": "Strong communication", "evaluationTypeDescription": "Communication"}
            ]
            """;

        doReturn(mockedJson).when(spyService).getInterviewsEvaluationsFromAiByPrompt(any(), any(),any());

        List<QuestionsAndAnswersForEvaluationDTO> qaList =
        List.of(new QuestionsAndAnswersForEvaluationDTO(
                "What is Java?",
                "A high-level, class-based, object-oriented programming language.",
                2,
                3
        ),

       new QuestionsAndAnswersForEvaluationDTO(
                "Explain polymorphism.",
                "The ability of an object to take many forms.",
                3,
                4
        ));
        PlaceholdersForInterviewEvaluationPromptDTO placeholders = mock(PlaceholdersForInterviewEvaluationPromptDTO.class);
        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),InterviewEvaluationPromptConstants.INTERVIEW_EVALUATION_PROMPT_CODE,InterviewEvaluationPromptConstants.INTERVIEW_EVALUATION_PROMPT, InterviewEvaluationPromptConstants.JSON_SCHEMA);


        List<EvaluationsAIResponseDTO> result = spyService.prepareEvaluationsDTOFromAiResponse(new InterviewEvaluationsRequestDTO(qaList), placeholders,prompt);

        assertNotNull(result);
        assertEquals(2, result.size());

        EvaluationsAIResponseDTO first = result.get(0);
        assertEquals("Backend", first.evaluationTypeDescription());
        assertEquals(95, first.score());
        assertTrue(first.feedback().contains("technical"));

        EvaluationsAIResponseDTO second = result.get(1);
        assertEquals("Communication", second.evaluationTypeDescription());
        assertEquals(88, second.score());
    }

    @Test
    void prepareEvaluationResponseFromAi_withMockFlagTrue_shouldParseJsonCorrectly() {
        evaluationServ = new EvaluationServImpl(
                evaluationRepository,
                evaluationMapper,
                interviewRepository,
                evaluationTypeRepository,
                true,
                aiRestClient
        );

        List<QuestionsAndAnswersForEvaluationDTO> qaList =
                List.of(new QuestionsAndAnswersForEvaluationDTO(
                                "What is Java?",
                                "A high-level, class-based, object-oriented programming language.",
                                2,
                                3
                        ),

                        new QuestionsAndAnswersForEvaluationDTO(
                                "Explain polymorphism.",
                                "The ability of an object to take many forms.",
                                3,
                                4
                        ));
        PlaceholdersForInterviewEvaluationPromptDTO placeholders = mock(PlaceholdersForInterviewEvaluationPromptDTO.class);

        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),InterviewPromptConstants.INTERVIEW_GENERATE_QUESTIONS_PROMPT_CODE, InterviewPromptConstants.INTERVIEW_QUESTION_GENERATION_PROMPT,InterviewPromptConstants.JSON_SCHEMA);

        List<EvaluationsAIResponseDTO> result = evaluationServ.prepareEvaluationsDTOFromAiResponse(new InterviewEvaluationsRequestDTO(qaList), placeholders,prompt);

        assertNotNull(result);
        assertEquals(6, result.size());

        EvaluationsAIResponseDTO first = result.get(0);
        assertEquals("Problem Solving", first.evaluationTypeDescription());
        assertEquals(78, first.score());
        assertTrue(first.feedback().contains("problem-solving"));
    }

    @Test
    void saveAIEvaluationResponse_shouldMapAndSaveCorrectly() {
        evaluationServ = new EvaluationServImpl(
                evaluationRepository,
                evaluationMapper,
                interviewRepository,
                evaluationTypeRepository,
                false,
                aiRestClient
        );

        EvaluationsAIResponseDTO ai1 = new EvaluationsAIResponseDTO(80.0, "strong understanding", "problem solving");
        EvaluationsAIResponseDTO ai2 = new EvaluationsAIResponseDTO(60.0, "he did well but he can improve", "communication");
        List<EvaluationsAIResponseDTO> aiList = List.of(ai1, ai2);

        EvaluationType techType = new EvaluationType();
        techType.setDescription("problem solving");

        EvaluationType softType = new EvaluationType();
        softType.setDescription("communication");

        Evaluation eval1 = new Evaluation();
        eval1.setEvaluationType(techType);

        Evaluation eval2 = new Evaluation();
        eval2.setEvaluationType(softType);

        List<Evaluation> evaluationList = List.of(eval1, eval2);


        when(evaluationMapper.mapAIEvaluationResponsesToEvaluations(aiList,evaluationList)).thenReturn(evaluationList);
        when(evaluationRepository.saveAll(any())).thenReturn(evaluationList);

        evaluationServ.saveAIEvaluationResponse(aiList,evaluationList);

        verify(evaluationRepository).saveAll(any());
        verify(evaluationMapper).mapAIEvaluationResponsesToEvaluations(aiList,evaluationList);

    }


    @Test
    void getAllEvaluationsByInterviewID_shouldReturnEvaluationList() {
        UUID interviewId = UUID.randomUUID();

        Evaluation eval1 = new Evaluation();
        Evaluation eval2 = new Evaluation();
        List<Evaluation> expectedEvaluations = List.of(eval1, eval2);

        when(evaluationRepository.findByInterviewId(interviewId)).thenReturn(expectedEvaluations);

        evaluationServ = new EvaluationServImpl(
                evaluationRepository,
                evaluationMapper,
                interviewRepository,
                evaluationTypeRepository,
                false,
                aiRestClient
        );

        List<Evaluation> result = evaluationServ.getAllEvaluationsByInterviewID(interviewId);

        assertEquals(2, result.size());
        assertEquals(expectedEvaluations, result);
    }

    @Test
    void getInterviewsEvaluationsFromAiByPrompt_should_return_expected_response() {

        evaluationServ = new EvaluationServImpl(
                evaluationRepository,
                evaluationMapper,
                interviewRepository,
                evaluationTypeRepository,
                true,
                aiRestClient
        );


        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),"test",INTERVIEW_EVALUATION_PROMPT,JSON_SCHEMA);

        QuestionsAndAnswersForEvaluationDTO qa = new QuestionsAndAnswersForEvaluationDTO(
                "What is OOP?", "Object Oriented Programming", 5, 5
        );
        InterviewEvaluationsRequestDTO requestDTO = new InterviewEvaluationsRequestDTO(

                List.of(qa)
        );

        CandidateDTO candidateDTO = new CandidateDTO(UUID.randomUUID(), "John Doe", null, 0, null, null, null, null, null, null, null, null, null, null, null);
        OfferDTO offerDTO = new OfferDTO(UUID.randomUUID(), "Java Developer", null, null);
        EvaluationType tech = new EvaluationType();
        tech.setId(UUID.randomUUID());
        tech.setDescription("Technical");
        tech.setCoefficient(1.0);

        EvaluationType comm = new EvaluationType();
        comm.setId(UUID.randomUUID());
        comm.setDescription("Communication");
        comm.setCoefficient(1.0);

        List<EvaluationType> evaluationTypes = List.of(tech, comm);

        PlaceholdersForInterviewEvaluationPromptDTO placeholders = new PlaceholdersForInterviewEvaluationPromptDTO(
                candidateDTO, offerDTO, evaluationTypes
        );



        String mockedResponse = "{\"evaluation\":\"Good\"}";

        when(aiRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/evaluationInterview")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(mockedResponse);

        String result = evaluationServ.getInterviewsEvaluationsFromAiByPrompt(requestDTO, placeholders,prompt);

        assertEquals(mockedResponse, result);

        verify(aiRestClient).post();
        verify(requestBodyUriSpec).uri("/evaluationInterview");
        verify(requestBodySpec).body(anyString());
        verify(requestBodySpec).retrieve();
        verify(responseSpec).body(String.class);
    }


}