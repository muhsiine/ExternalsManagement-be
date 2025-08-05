package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.dto.placeholdersForInterviewQuestionsPromptDTO;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.dto.AIQuestionResponseDTO;
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


}
