package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.interview.entity.Interview;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
                {"score": 95, "feedback": "Excellent technical depth", "evaluationTypeDescription": "Backend"},
                {"score": 88, "feedback": "Strong communication", "evaluationTypeDescription": "Communication"}
            ]
            """;

        doReturn(mockedJson).when(spyService).getInterviewsEvaluationsFromAiByPrompt(any(), any());

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

        List<EvaluationsAIResponseDTO> result = spyService.prepareEvaluationsDTOFromAiResponse(new InterviewEvaluationsRequestDTO(qaList), placeholders);

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

        List<EvaluationsAIResponseDTO> result = evaluationServ.prepareEvaluationsDTOFromAiResponse(new InterviewEvaluationsRequestDTO(qaList), placeholders);

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

}