package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.commons.constants.InterviewPromptConstants;
import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.cv.dto.FileDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.dto.placeholdersForInterviewQuestionsPromptDTO;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.dto.AIQuestionResponseDTO;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.entity.Question;
import ma.nttdata.externals.module.interview.mapper.QuestionMapper;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
import ma.nttdata.externals.module.interview.repository.QuestionRepository;
import ma.nttdata.externals.module.interview.service.TextToSpeechServ;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

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

    private QuestionServImpl questionServWithMockFlagTrue;
    private QuestionServImpl questionServWithMockFlagFalse;

    @BeforeEach
    void setUp() {
        questionServWithMockFlagTrue = new QuestionServImpl(
                questionRepository, questionMapper, interviewRepository, true, aiRestClient, textToSpeechServ
        );

        questionServWithMockFlagFalse = new QuestionServImpl(
                questionRepository, questionMapper, interviewRepository, false, aiRestClient, textToSpeechServ
        );
    }




    @Test
    void PrepareQuestionsFromAIResponse_shouldParseJsonCorrectly() {
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


        List<QuestionDTO> questions = questionServWithMockFlagTrue.prepareQuestionsFromAIResponse(generateQuestionsInfo, evaluationTypes,prompt);


        assertNotNull(questions);
        assertEquals(15, questions.size());
        assertTrue(questions.stream().allMatch(q -> q.description() != null));
    }

    @Test
    void prepareQuestionsFromAIResponse_WithMockFlagTrue_ShouldReturnMockQuestions() throws Exception {
        CandidateDTO candidate = new CandidateDTO(
                null,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                5,
                GenderEnum.M,
                "Java",
                "Experienced Java developer",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                List.of(),
                List.of()
        );
        OfferDTO offer = new OfferDTO(
                null,
                "Java Developer",
                "Develop Java backend services",
                null,
                List.of()
        );
        placeholdersForInterviewQuestionsPromptDTO request = new placeholdersForInterviewQuestionsPromptDTO(
                candidate, offer, 4, 30
        );

        List<EvaluationTypeDTO> evaluationTypes = List.of();
        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),InterviewPromptConstants.INTERVIEW_GENERATE_QUESTIONS_PROMPT_CODE,
                InterviewPromptConstants.INTERVIEW_QUESTION_GENERATION_PROMPT,InterviewPromptConstants.JSON_SCHEMA);

        List<QuestionDTO> questions = questionServWithMockFlagTrue.prepareQuestionsFromAIResponse(request, evaluationTypes, prompt);

        assertNotNull(questions);
        assertFalse(questions.isEmpty());
        assertEquals(15, questions.size());
        assertEquals("Given your 8 years of experience with Java and Spring Boot, can you walk me through how you would design a microservices architecture for a high-traffic e-commerce platform? Focus on service decomposition and inter-service communication strategies.", questions.get(0).description());
        assertEquals(5, questions.get(0).durationInMinutes());
    }

    @Test
    void prepareQuestionsFromAIResponse_WithMockFlagFalse_ShouldCallGenerateInterviewQuestionsByPrompt() throws Exception {

        CandidateDTO candidate = new CandidateDTO(
                null,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                5,
                GenderEnum.M,
                "Java",
                "Experienced Java developer",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                List.of(),
                List.of()
        );
        OfferDTO offer = new OfferDTO(
                null,
                "Java Developer",
                "Develop Java backend services",
                null,
                List.of()
        );

        placeholdersForInterviewQuestionsPromptDTO request = new placeholdersForInterviewQuestionsPromptDTO(
                candidate, offer, 4, 30
        );

        List<EvaluationTypeDTO> evaluationTypes = List.of();
        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),InterviewPromptConstants.INTERVIEW_GENERATE_QUESTIONS_PROMPT_CODE,
                InterviewPromptConstants.INTERVIEW_QUESTION_GENERATION_PROMPT,InterviewPromptConstants.JSON_SCHEMA);

        String fakeJson = """
            [
                {"description":"What is Java?","durationInMinutes":"3"},
                {"description":"Explain REST APIs.","durationInMinutes":"4"}
            ]
            """;

        QuestionServImpl spyServ = spy(new QuestionServImpl(
                questionRepository,
                questionMapper,
                interviewRepository,
                false,
                aiRestClient,
                textToSpeechServ
        ));

        doReturn(fakeJson).when(spyServ).generateInterviewQuestionsByPrompt(request, evaluationTypes, prompt);

        List<QuestionDTO> questions = spyServ.prepareQuestionsFromAIResponse(request, evaluationTypes, prompt);

        assertNotNull(questions);
        assertEquals(2, questions.size());
        assertEquals("What is Java?", questions.get(0).description());
        assertEquals(3, questions.get(0).durationInMinutes());
        assertEquals("Explain REST APIs.", questions.get(1).description());
        assertEquals(4, questions.get(1).durationInMinutes());

        verify(spyServ).generateInterviewQuestionsByPrompt(request, evaluationTypes, prompt);
    }


    @Test
    void should_generateInterviewQuestionsByPrompt(){
        CandidateDTO candidate = new CandidateDTO(
                UUID.randomUUID(),
                "John Doe",
                LocalDate.of(1990, 5, 15),
                5,
                GenderEnum.M,
                "Java",
                "Experienced Java developer",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                List.of(),
                List.of()
        );

        OfferDTO offer = new OfferDTO(
                UUID.randomUUID(),
                "Java Backend Developer",
                "We are looking for a skilled Java backend developer",
                null,
                List.of()
        );

        placeholdersForInterviewQuestionsPromptDTO placeholders = new placeholdersForInterviewQuestionsPromptDTO(
                candidate,
                offer,
                5,
                60
        );
        EvaluationTypeDTO evaluationType1 = new EvaluationTypeDTO(UUID.randomUUID(), "Technical", 1.0);
        EvaluationTypeDTO evaluationType2 = new EvaluationTypeDTO(UUID.randomUUID(), "Communication", 2.0);

        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),
                InterviewPromptConstants.INTERVIEW_GENERATE_QUESTIONS_PROMPT_CODE,InterviewPromptConstants.INTERVIEW_QUESTION_GENERATION_PROMPT,
                InterviewPromptConstants.JSON_SCHEMA);

        String expectedResponse = "[{\"description\":\"Explain Java Streams\",\"durationInMinutes\":5}]";
        when(aiRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/generateInterviewQuestions")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(expectedResponse);

        String result = questionServWithMockFlagTrue.generateInterviewQuestionsByPrompt(placeholders,List.of(evaluationType1,evaluationType2),prompt);

        assertEquals(expectedResponse,result);

        verify(aiRestClient).post();
        verify(requestBodyUriSpec).uri("/generateInterviewQuestions");

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        String capturedBody = bodyCaptor.getValue();
        assertTrue(capturedBody.contains(candidate.toString()));
        assertTrue(capturedBody.contains(offer.toString()));
        assertTrue(capturedBody.contains(String.valueOf(placeholders.numberOfQuestions())));
        assertTrue(capturedBody.contains(String.valueOf(placeholders.estimatedDuration())));
        assertTrue(capturedBody.contains(List.of(evaluationType1,evaluationType2).toString()));
    }

    @Test
    void should_saveAllQuestions(){
        Interview interview = new Interview();
        interview.setId(UUID.randomUUID());
        QuestionDTO questionDTO1 = new QuestionDTO(UUID.randomUUID(), "What is Java?", 5,interview.getId(),null);
        QuestionDTO questionDTO2 = new QuestionDTO(UUID.randomUUID(), "Explain Streams.", 5,interview.getId(),null);
        List<QuestionDTO> questionsDTO = List.of(questionDTO1, questionDTO2);

        Question question1 = new Question();
        question1.setId(UUID.randomUUID());
        question1.setDescription(questionDTO1.description());
        question1.setInterview(interview);
        Question question2 = new Question();
        question2.setId(UUID.randomUUID());
        question2.setDescription(questionDTO2.description());
        question2.setInterview(interview);
        List<Question> questions = List.of(question1, question2);

        when(questionMapper.toEntity(questionDTO1)).thenReturn(question1);
        when(questionMapper.toEntity(questionDTO2)).thenReturn(question2);
        when(questionMapper.toDtoList(anyList())).thenReturn(questionsDTO);

        when(questionRepository.saveAll(questions)).thenReturn(questions);

        List<QuestionDTO> result =questionServWithMockFlagTrue.saveAllQuestions(questionsDTO);

        assertEquals(questionsDTO.size(), result.size());
        assertEquals(questionsDTO, result);

        verify(questionMapper).toEntity(questionDTO1);
        verify(questionMapper).toEntity(questionDTO2);
        verify(questionRepository).saveAll(questions);
        verify(questionMapper).toDtoList(questions);
    }

    @Test
    void should_findAllQuestionsByInterviewId() {
        UUID interviewId = UUID.randomUUID();

        Question q1 = new Question();
        q1.setId(UUID.randomUUID());
        q1.setDescription("What is Java?");

        Question q2 = new Question();
        q2.setId(UUID.randomUUID());
        q2.setDescription("Explain Streams.");

        List<Question> questions = List.of(q1, q2);

        when(questionRepository.findByInterviewId(interviewId)).thenReturn(questions);

        List<Question> result = questionServWithMockFlagTrue.findAllQuestionsByInterviewId(interviewId);

        assertEquals(2, result.size());
        assertEquals(questions, result);
        verify(questionRepository).findByInterviewId(interviewId);
    }

    @Test
    void should_findAllQuestionsDTOSByInterviewId() {

        UUID interviewId = UUID.randomUUID();
        Interview interview = new Interview();
        interview.setId(UUID.randomUUID());

        Question q1 = new Question();
        q1.setId(UUID.randomUUID());
        q1.setDescription("What is Java?");

        Question q2 = new Question();
        q2.setId(UUID.randomUUID());
        q2.setDescription("Explain Streams.");

        List<Question> questions = List.of(q1, q2);

        QuestionDTO qdto1 = new QuestionDTO(q1.getId(), q1.getDescription(), 5,interviewId,null);
        QuestionDTO qdto2 = new QuestionDTO(q2.getId(), q2.getDescription(), 5,interviewId,null);
        List<QuestionDTO> dtos = List.of(qdto1, qdto2);

        when(questionRepository.findByInterviewId(interviewId)).thenReturn(questions);
        when(questionMapper.toDtoList(questions)).thenReturn(dtos);

        List<QuestionDTO> result = questionServWithMockFlagTrue.findAllQuestionsDTOSByInterviewId(interviewId);

        assertEquals(2, result.size());
        assertEquals(dtos, result);
        verify(questionRepository).findByInterviewId(interviewId);
        verify(questionMapper).toDtoList(questions);
    }

    @Test
    void should_generateInterviewQuestionAudio() {
        questionServ = new QuestionServImpl(
                questionRepository,
                questionMapper,
                interviewRepository,
                true,
                aiRestClient,textToSpeechServ);

        String text = "What is Java?";
        byte[] audioBytes = "fake-audio".getBytes();

        when(textToSpeechServ.speak(text)).thenReturn(audioBytes);

        ResponseEntity<byte[]> response = questionServWithMockFlagTrue.generateInterviewQuestionAudio(text);

        assertEquals(audioBytes.length, response.getHeaders().getContentLength());
        assertEquals(MediaType.valueOf("audio/mpeg"), response.getHeaders().getContentType());
        assertArrayEquals(audioBytes, response.getBody());

        verify(textToSpeechServ).speak(text);
    }

}
