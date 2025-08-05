package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.dto.placeholdersForInterviewQuestionsPromptDTO;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.dto.AIQuestionResponseDTO;
import ma.nttdata.externals.module.interview.entity.Answer;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.entity.Question;
import ma.nttdata.externals.module.interview.mapper.QuestionMapper;
import ma.nttdata.externals.module.interview.repository.AnswerRepository;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
import ma.nttdata.externals.module.interview.repository.QuestionRepository;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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

    private QuestionServImpl questionServ;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private AnswerRepository answerRepository;

    private boolean mockFlag = true;



    @Test
    void PrepareQuestionsFromAIResponse_shouldParseJsonCorrectly() {

        questionServ = new QuestionServImpl(
                questionRepository,
                questionMapper,
                interviewRepository,
                true,
                aiRestClient,
                answerRepository);
        CandidateDTO candidateDTO = new CandidateDTO(UUID.randomUUID(), null, null, 0, null, null, null, null, null, null, null, null, null, null, null);
        OfferDTO offerDTO = new OfferDTO(UUID.randomUUID(), null, null, null);

        placeholdersForInterviewQuestionsPromptDTO generateQuestionsInfo = new placeholdersForInterviewQuestionsPromptDTO(
                candidateDTO,
                offerDTO,
                3,
                30
        );

        List<EvaluationTypeDTO> evaluationTypes = List.of(
                new EvaluationTypeDTO(UUID.randomUUID(), "Technical", 1.0)
        );


        List<QuestionDTO> questions = questionServ.prepareQuestionsFromAIResponse(generateQuestionsInfo, evaluationTypes);


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
                aiRestClient,
                answerRepository);
        String mockedJson = """
        [
            {"description": "What is Java?", "durationInMinutes": "3"},
            {"description": "Explain REST APIs.", "durationInMinutes": "4"}
        ]
        """;



        questionServ = spy(questionServ);
        doReturn(mockedJson)
                .when(questionServ)
                .generateInterviewQuestionsByPrompt(any(), any());

        CandidateDTO candidateDTO = new CandidateDTO(UUID.randomUUID(), "John", null, 0, null, null, null, null, null, null, null, null, null, null, null);
        OfferDTO offerDTO = new OfferDTO(UUID.randomUUID(), "Backend", null, null);
        placeholdersForInterviewQuestionsPromptDTO promptDTO =
                new placeholdersForInterviewQuestionsPromptDTO(candidateDTO, offerDTO, 2, 30);

        List<EvaluationTypeDTO> evaluationTypes = List.of(
                new EvaluationTypeDTO(UUID.randomUUID(), "Technical", 1.0)
        );

        List<QuestionDTO> result = questionServ.prepareQuestionsFromAIResponse(promptDTO, evaluationTypes);

        assertEquals(2, result.size());
        assertEquals("What is Java?", result.get(0).description());
        assertEquals(3, result.get(0).durationInMinutes());
        assertEquals("Explain REST APIs.", result.get(1).description());
        assertEquals(4, result.get(1).durationInMinutes());
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

    @Test
    void should_generate_questions_prompt_successfully() {

        questionServ = new QuestionServImpl(
                questionRepository,
                questionMapper,
                interviewRepository,
                false,
                aiRestClient,
                answerRepository);
        CandidateDTO candidateDTO = new CandidateDTO(UUID.randomUUID(), "John Doe", null, 0, null, null, null, null, null, null, null, null, null, null, null);
        OfferDTO offerDTO = new OfferDTO(UUID.randomUUID(), "Java Developer", null, null);
        placeholdersForInterviewQuestionsPromptDTO promptDTO = new placeholdersForInterviewQuestionsPromptDTO(
                candidateDTO, offerDTO, 5, 30
        );

        List<EvaluationTypeDTO> evaluationTypes = List.of(
                new EvaluationTypeDTO(UUID.randomUUID(), "Technical", 1.0),
                new EvaluationTypeDTO(UUID.randomUUID(), "Communication", 1.0)
        );

        String mockedResponse = "[{\"description\":\"What is OOP?\",\"durationInMinutes\":5}]";

        when(aiRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/generateInterviewQuestions")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(mockedResponse);



        String result = questionServ.generateInterviewQuestionsByPrompt(promptDTO, evaluationTypes);

        assertEquals(mockedResponse, result);
        verify(aiRestClient).post();
        verify(requestBodyUriSpec).uri("/generateInterviewQuestions");
        verify(requestBodySpec).body(anyString());
        verify(requestBodySpec).retrieve();
        verify(responseSpec).body(String.class);
    }

    @Test
    void shouldSaveAllQuestionsSuccessfully() {
        questionServ = new QuestionServImpl(
                questionRepository,
                questionMapper,
                interviewRepository,
                false,
                aiRestClient,
                answerRepository);
        UUID interviewId = UUID.randomUUID();
        UUID answerId = UUID.randomUUID();
        UUID questionId1 = UUID.randomUUID();
        UUID questionId2 = UUID.randomUUID();
        QuestionDTO qdto1 = new QuestionDTO(questionId1, "Question 1", 5, interviewId, answerId);
        QuestionDTO qdto2 = new QuestionDTO(questionId2, "Question 2", 3, interviewId, answerId);
        List<QuestionDTO> questionDTOs = List.of(qdto1, qdto2);

        Question question1 = new Question();
        question1.setId(questionId1);
        question1.setDescription(qdto1.description());
        question1.setDurationInMinutes(qdto1.durationInMinutes());
        Interview interview = new Interview();
        interview.setId(interviewId);
        question1.setInterview(interview);

        Question question2 = new Question();
        question2.setId(questionId2);
        question2.setDescription(qdto2.description());
        question2.setDurationInMinutes(qdto2.durationInMinutes());
        interview.setId(interviewId);
        question2.setInterview(interview);

        Answer savedAnswer = new Answer();
        savedAnswer.setId(answerId);
        question1.setAnswer(savedAnswer);
        question2.setAnswer(savedAnswer);
        given(answerRepository.save(any(Answer.class))).willReturn(savedAnswer);

        given(questionMapper.toEntity(qdto1)).willReturn(question1);
        given(questionMapper.toEntity(qdto2)).willReturn(question2);

        List<Question> savedQuestions = List.of(question1, question2);
        given(questionRepository.saveAll(anyList())).willReturn(savedQuestions);

        given(questionMapper.toDtoList(savedQuestions)).willReturn(questionDTOs);

        List<QuestionDTO> result = questionServ.saveAllQuestions(questionDTOs);

        QuestionDTO first = result.get(0);
        assertEquals(questionId1, first.id());
        assertEquals("Question 1", first.description());
        assertEquals(5, first.durationInMinutes());
        assertEquals(interviewId, first.interviewId());
        assertEquals(answerId, first.answerId());

        QuestionDTO second = result.get(1);
        assertEquals(questionId2, second.id());
        assertEquals("Question 2", second.description());
        assertEquals(3, second.durationInMinutes());
        assertEquals(interviewId, second.interviewId());
        assertEquals(answerId, second.answerId());

        result.forEach(q -> {
            assertNotNull(q.description());
            assertNotNull(q.durationInMinutes());
            assertNotNull(q.interviewId());
            assertNotNull(q.answerId());
        });

        assertEquals(interviewId, question1.getInterview().getId());
        assertEquals(answerId, question1.getAnswer().getId());
        assertEquals(interviewId, question2.getInterview().getId());
        assertEquals(answerId, question2.getAnswer().getId());

        verify(answerRepository, times(2)).save(any(Answer.class));
        verify(questionRepository).saveAll(anyList());
        verify(questionMapper).toDtoList(savedQuestions);
    }

    @Test
    void shouldFindAllQuestionsByInterviewId() {
        questionServ = new QuestionServImpl(
                questionRepository,
                questionMapper,
                interviewRepository,
                false,
                aiRestClient,
                answerRepository);
        UUID interviewId = UUID.randomUUID();

        Question question1 = new Question();
        question1.setId(UUID.randomUUID());
        question1.setDescription("Q1");
        question1.setDurationInMinutes(5);
        Interview interview = new Interview();
        interview.setId(interviewId);
        interview.setEstimatedDuration(50);
        interview.setNumberOfQuestions(15);
        interview.setComment("test");
        question1.setInterview(interview);

        Question question2 = new Question();
        question2.setId(UUID.randomUUID());
        question2.setDescription("Q2");
        question2.setDurationInMinutes(3);
        question2.setInterview(interview);

        List<Question> questions = List.of(question1, question2);

        given(questionRepository.findByInterviewId(interviewId)).willReturn(questions);

        List<Question> result = questionServ.findAllQuestionsByInterviewId(interviewId);

        assertEquals(2, result.size());
        assertTrue(result.contains(question1));
        assertTrue(result.contains(question2));

        verify(questionRepository).findByInterviewId(interviewId);
    }

    @Test
    void shouldFindAllQuestionDTOsByInterviewId() {
        questionServ = new QuestionServImpl(
                questionRepository,
                questionMapper,
                interviewRepository,
                false,
                aiRestClient,
                answerRepository);
        UUID interviewId = UUID.randomUUID();

        Question question1 = new Question();
        question1.setId(UUID.randomUUID());
        question1.setDescription("Q1");
        question1.setDurationInMinutes(5);
        Interview interview = new Interview();
        interview.setId(interviewId);
        question1.setInterview(interview);

        Question question2 = new Question();
        question2.setId(UUID.randomUUID());
        question2.setDescription("Q2");
        question2.setDurationInMinutes(3);
        question2.setInterview(interview);

        List<Question> questions = List.of(question1, question2);

        QuestionDTO dto1 = new QuestionDTO(question1.getId(), question1.getDescription(), question1.getDurationInMinutes(), interviewId, null);
        QuestionDTO dto2 = new QuestionDTO(question2.getId(), question2.getDescription(), question2.getDurationInMinutes(), interviewId, null);
        List<QuestionDTO> dtos = List.of(dto1, dto2);

        given(questionRepository.findByInterviewId(interviewId)).willReturn(questions);
        given(questionMapper.toDtoList(questions)).willReturn(dtos);

        List<QuestionDTO> result = questionServ.findAllQuestionsDTOSByInterviewId(interviewId);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        verify(questionRepository).findByInterviewId(interviewId);
        verify(questionMapper).toDtoList(questions);
    }



}
