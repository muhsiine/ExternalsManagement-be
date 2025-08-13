package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.mapper.EvaluationMapper;
import ma.nttdata.externals.module.interview.service.EvaluationServ;
import ma.nttdata.externals.module.interview.service.InterviewServ;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InterviewEvaluationUtilServImplTest {

    @Mock
    private EvaluationServ evaluationServ;
    @Mock
    private InterviewServ interviewServ;
    @Mock
    private EvaluationMapper evaluationMapper;

    private InterviewEvaluationUtilServImpl interviewEvaluationUtilServ;

    @BeforeEach
    void setUp() {
        interviewEvaluationUtilServ = new InterviewEvaluationUtilServImpl(
                evaluationServ,
                interviewServ,
                evaluationMapper
        );
    }

    @Test
    void should_return_saved_evaluations() throws Exception {
        UUID interviewId = UUID.randomUUID();

        QuestionsAndAnswersForEvaluationDTO questionsAndAnswersForEvaluation1 =
                new QuestionsAndAnswersForEvaluationDTO(
                        "what is the useState hook",
                        "use state is",
                        2,
                        3);
        QuestionsAndAnswersForEvaluationDTO questionsAndAnswersForEvaluation2 =
                new QuestionsAndAnswersForEvaluationDTO(
                        "what is the useEffect hook",
                        "useEffect is",
                        2,
                        3);

        InterviewEvaluationsRequestDTO interviewEvaluationsRequest =
                new InterviewEvaluationsRequestDTO(List.of(questionsAndAnswersForEvaluation1, questionsAndAnswersForEvaluation2));

        Evaluation evaluation = new Evaluation();
        evaluation.setFeedback("nothing");
        evaluation.setScore(60.0);

        EvaluationType evaluationType1 = new EvaluationType();
        evaluationType1.setId(UUID.randomUUID());
        evaluationType1.setDescription("React Skills");
        evaluationType1.setCoefficient(3.0);

        EvaluationType evaluationType2 = new EvaluationType();
        evaluationType2.setId(UUID.randomUUID());
        evaluationType2.setDescription("Java Skills");
        evaluationType2.setCoefficient(2.0);

        List<EvaluationType> evaluationTypes = List.of(evaluationType1, evaluationType2);

        CandidateDTO candidateDTO = new CandidateDTO(
                UUID.randomUUID(),
                "John",
                null,
                10,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        OfferDTO offerDTO = new OfferDTO(
                UUID.randomUUID(),
                "Backend Developer",
                "just testing",
                null
        );

        PlaceholdersForInterviewEvaluationPromptDTO placeholders = new PlaceholdersForInterviewEvaluationPromptDTO(candidateDTO, offerDTO, evaluationTypes);


        List<EvaluationsAIResponseDTO> aiEvaluationResponse = List.of(
                new EvaluationsAIResponseDTO(4.0, "Good understanding of hooks", "react skills"),
                new EvaluationsAIResponseDTO(3.5, "Basic understanding of lifecycle", "react skills")
        );

        List<Evaluation> existingEvaluations = List.of(evaluation);
        List<Evaluation> savedEvaluations = List.of(evaluation);

        when(interviewServ.getInterviewEvaluationPlaceholders(interviewId)).thenReturn(placeholders);
        when(evaluationServ.prepareEvaluationsDTOFromAiResponse(eq(interviewEvaluationsRequest), eq(placeholders))).thenReturn(aiEvaluationResponse);
        when(evaluationServ.getAllEvaluationsByInterviewID(interviewId)).thenReturn(existingEvaluations);
        when(evaluationServ.saveAIEvaluationResponse(aiEvaluationResponse, existingEvaluations)).thenReturn(savedEvaluations);

        List<Evaluation> result = interviewEvaluationUtilServ.prepareInterviewEvaluation(interviewId, interviewEvaluationsRequest);


        assertEquals(savedEvaluations, result);
        verify(interviewServ).getInterviewEvaluationPlaceholders(interviewId);
        verify(evaluationServ).prepareEvaluationsDTOFromAiResponse(interviewEvaluationsRequest, placeholders);
        verify(evaluationServ).getAllEvaluationsByInterviewID(interviewId);
        verify(evaluationServ).saveAIEvaluationResponse(aiEvaluationResponse, existingEvaluations);
    }


    @Test
    void should_return_interview_evaluations() throws Exception {

        UUID interviewId = UUID.randomUUID();

        EvaluationTypeDTO evaluationTypeDTO = new EvaluationTypeDTO(
                UUID.randomUUID(),
                "just testing",
                3.0
        );

        FullEvaluationDTO fullEvaluationDTO = new FullEvaluationDTO(
                UUID.randomUUID(),
                60.0,
                "just testing",
                interviewId,
                evaluationTypeDTO
        );

        LocalDateTime dateTime = LocalDateTime.now();
        InterviewEvaluationDTO interviewEvaluation = new InterviewEvaluationDTO(
                interviewId,
                "test test",
                "backend dev",
                dateTime,
                50,
                List.of(fullEvaluationDTO)
        );
        Interview interview = new Interview();
        interview.setId(interviewId);
        interview.setScheduledAt(dateTime);

        EvaluationType evaluationType = new EvaluationType();
        evaluationType.setId(UUID.randomUUID());
        evaluationType.setDescription("nothing");
        evaluationType.setCoefficient(3.0);

        Evaluation evaluation = new Evaluation(
                UUID.randomUUID(),
                60.0,
                "just testing",
                interview,
                evaluationType
        );

        when(evaluationServ.getAllEvaluationsByInterviewID(interviewId)).thenReturn(List.of(evaluation));

        when(evaluationMapper.mapEvaluationToInterviewEvaluation(List.of(evaluation)))
                .thenReturn(interviewEvaluation);

        InterviewEvaluationDTO result = interviewEvaluationUtilServ.getInterviewEvaluations(interviewId);

        assertThat(result.scheduledAt()).isEqualTo(interview.getScheduledAt());

        verify(evaluationServ).getAllEvaluationsByInterviewID(interviewId);
        verify(evaluationMapper).mapEvaluationToInterviewEvaluation(List.of(evaluation));
    }

}
