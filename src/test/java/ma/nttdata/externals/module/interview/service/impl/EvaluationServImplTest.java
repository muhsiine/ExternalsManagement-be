package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.module.interview.dto.AiEvaluationResponseDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.dto.InterviewEvaluationPlaceholdersDTO;
import ma.nttdata.externals.module.interview.dto.QuestionsAndAnswersForEvaluationDTO;
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
        assertEquals(2, result.size());

        AiEvaluationResponseDTO first = result.get(0);
        assertEquals("Problem Solving", first.evaluationType());
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

        AiEvaluationResponseDTO ai1 = new AiEvaluationResponseDTO(80.0, "strong understanding", "problem solving");
        AiEvaluationResponseDTO ai2 = new AiEvaluationResponseDTO(60.0, "he did well but he can improve", "communication");
        List<AiEvaluationResponseDTO> aiList = List.of(ai1, ai2);

        EvaluationType techType = new EvaluationType();
        techType.setDescription("problem solving");

        EvaluationType softType = new EvaluationType();
        softType.setDescription("communication");

        Evaluation eval1 = new Evaluation();
        eval1.setEvaluationType(techType);

        Evaluation eval2 = new Evaluation();
        eval2.setEvaluationType(softType);

        List<Evaluation> evaluationList = List.of(eval1, eval2);

        InterviewEvaluationPlaceholdersDTO placeholders = mock(InterviewEvaluationPlaceholdersDTO.class);
        when(placeholders.evaluations()).thenReturn(evaluationList);

        when(evaluationRepository.saveAll(any())).thenReturn(evaluationList);

        List<Evaluation> saved = evaluationServ.saveAIEvaluationResponse(aiList, placeholders);

        assertEquals(2, saved.size());
        assertEquals("strong understanding", saved.get(0).getFeedback());
        assertEquals(80.0, saved.get(0).getScore());
        assertEquals("he did well but he can improve", saved.get(1).getFeedback());
        assertEquals(60.0, saved.get(1).getScore());
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
    void getAllEvaluationsDTOByInterviewID_shouldReturnEvaluationDTOList() {
        evaluationServ = new EvaluationServImpl(
                evaluationRepository,
                evaluationMapper,
                interviewRepository,
                evaluationTypeRepository,
                false,
                aiRestClient
        );

        Double score = 80.0;
        String feedback1 = "he is good";
        String feedback2 = "needs improvement";

        UUID interviewId = UUID.randomUUID();
        UUID eval1Id = UUID.randomUUID();
        UUID eval2Id = UUID.randomUUID();
        UUID evalType1Id = UUID.randomUUID();
        UUID evalType2Id = UUID.randomUUID();

        Interview interview = new Interview();
        interview.setId(interviewId);

        Evaluation eval1 = new Evaluation();
        eval1.setId(eval1Id);
        eval1.setInterview(interview);
        eval1.setScore(score);
        eval1.setInterview(interview);
        eval1.setFeedback(feedback1);

        Evaluation eval2 = new Evaluation();
        eval2.setId(eval2Id);
        eval2.setScore(score);
        eval2.setFeedback(feedback2);

        eval2.setInterview(interview);
        List<Evaluation> evaluations = List.of(eval1, eval2);


        EvaluationDTO dto1 = new EvaluationDTO(eval1Id, score, "he is good", interviewId, evalType1Id);
        EvaluationDTO dto2 = new EvaluationDTO(eval2Id, score, "needs improvement", interviewId, evalType2Id);
        List<EvaluationDTO> expectedDTOs = List.of(dto1, dto2);

        when(evaluationRepository.findByInterviewId(interviewId)).thenReturn(evaluations);
        when(evaluationMapper.toDtoList(evaluations)).thenReturn(expectedDTOs);

        List<EvaluationDTO> result = evaluationServ.getAllEvaluationsDTOByInterviewID(interviewId);

        assertEquals(2, result.size());

        assertThat(result.get(0))
                .extracting(EvaluationDTO::id, EvaluationDTO::score, EvaluationDTO::feedback, EvaluationDTO::interviewId, EvaluationDTO::evaluationTypeId)
                .containsExactly(eval1Id, score, feedback1, interviewId, evalType1Id);

        assertThat(result.get(1))
                .extracting(EvaluationDTO::id, EvaluationDTO::score, EvaluationDTO::feedback, EvaluationDTO::interviewId, EvaluationDTO::evaluationTypeId)
                .containsExactly(eval2Id, score, feedback2, interviewId, evalType2Id);
    }

}
