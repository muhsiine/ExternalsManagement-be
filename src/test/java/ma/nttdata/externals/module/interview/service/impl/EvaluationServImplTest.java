package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.module.interview.dto.AiEvaluationResponseDTO;
import ma.nttdata.externals.module.interview.dto.InterviewEvaluationPlaceholdersDTO;
import ma.nttdata.externals.module.interview.dto.QuestionsAndAnswersForEvaluationDTO;
import ma.nttdata.externals.module.interview.mapper.EvaluationMapper;
import ma.nttdata.externals.module.interview.repository.AnswerRepository;
import ma.nttdata.externals.module.interview.repository.EvaluationRepository;
import ma.nttdata.externals.module.interview.repository.EvaluationTypeRepository;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
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
                {"score": 95, "feedback": "Excellent technical depth", "evaluationType": "Backend"},
                {"score": 88, "feedback": "Strong communication", "evaluationType": "Communication"}
            ]
            """;

        doReturn(mockedJson).when(spyService).getInterviewsEvaluationsFromAiByPrompt(any(), any());

        List<QuestionsAndAnswersForEvaluationDTO> qaList = List.of();
        InterviewEvaluationPlaceholdersDTO placeholders = mock(InterviewEvaluationPlaceholdersDTO.class);

        List<AiEvaluationResponseDTO> result = spyService.prepareEvaluationResponseFromAi(qaList, placeholders);

        assertNotNull(result);
        assertEquals(2, result.size());

        AiEvaluationResponseDTO first = result.get(0);
        assertEquals("Backend", first.evaluationType());
        assertEquals(95, first.score());
        assertTrue(first.feedback().contains("technical"));

        AiEvaluationResponseDTO second = result.get(1);
        assertEquals("Communication", second.evaluationType());
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

        List<QuestionsAndAnswersForEvaluationDTO> qaList = List.of();
        InterviewEvaluationPlaceholdersDTO placeholders = mock(InterviewEvaluationPlaceholdersDTO.class);

        List<AiEvaluationResponseDTO> result = evaluationServ.prepareEvaluationResponseFromAi(qaList, placeholders);

        assertNotNull(result);
        assertEquals(5, result.size());

        AiEvaluationResponseDTO first = result.get(0);
        assertEquals("Problem Solving", first.evaluationType());
        assertEquals(78, first.score());
        assertTrue(first.feedback().contains("problem-solving"));
    }



}
