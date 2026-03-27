package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.offer.entity.Offer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationMapperTest {


    @Mock
    private EvaluationTypeMapper evaluationTypeMapper;

    private EvaluationMapper evaluationMapper;

    @BeforeEach
    void setUp() {
        evaluationMapper = new EvaluationMapperImpl();

        ReflectionTestUtils.setField(evaluationMapper, "evaluationTypeMapper", evaluationTypeMapper);
    }


    @Test
    void testToDto() {
        // Arrange
        UUID evaluationId = UUID.randomUUID();
        UUID interviewId = UUID.randomUUID();
        UUID evaluationTypeId = UUID.randomUUID();
        
        Interview interview = new Interview();
        interview.setId(interviewId);
        
        EvaluationType evaluationType = new EvaluationType();
        evaluationType.setId(evaluationTypeId);
        
        Evaluation evaluation = new Evaluation();
        evaluation.setId(evaluationId);
        evaluation.setScore(4.5);
        evaluation.setFeedback("Good technical skills");
        evaluation.setInterview(interview);
        evaluation.setEvaluationType(evaluationType);

        // Act
        EvaluationDTO dto = evaluationMapper.toDto(evaluation);

        // Assert
        assertNotNull(dto);
        assertEquals(evaluation.getId(), dto.id());
        assertEquals(evaluation.getScore(), dto.score());
        assertEquals(evaluation.getFeedback(), dto.feedback());
        assertEquals(interviewId, dto.interviewId());
        assertEquals(evaluationTypeId, dto.evaluationTypeId());
    }

    @Test
    void testToDtoList() {
        // Arrange
        UUID evaluationId1 = UUID.randomUUID();
        UUID evaluationId2 = UUID.randomUUID();
        UUID interviewId = UUID.randomUUID();
        UUID evaluationTypeId = UUID.randomUUID();
        
        Interview interview = new Interview();
        interview.setId(interviewId);
        
        EvaluationType evaluationType = new EvaluationType();
        evaluationType.setId(evaluationTypeId);
        
        Evaluation evaluation1 = new Evaluation();
        evaluation1.setId(evaluationId1);
        evaluation1.setScore(4.0);
        evaluation1.setFeedback("Evaluation 1");
        evaluation1.setInterview(interview);
        evaluation1.setEvaluationType(evaluationType);
        
        Evaluation evaluation2 = new Evaluation();
        evaluation2.setId(evaluationId2);
        evaluation2.setScore(3.5);
        evaluation2.setFeedback("Evaluation 2");
        evaluation2.setInterview(interview);
        evaluation2.setEvaluationType(evaluationType);
        
        List<Evaluation> evaluations = new ArrayList<>();
        evaluations.add(evaluation1);
        evaluations.add(evaluation2);

        // Act
        List<EvaluationDTO> dtos = evaluationMapper.toDtoList(evaluations);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(evaluation1.getId(), dtos.get(0).id());
        assertEquals(evaluation1.getScore(), dtos.get(0).score());
        assertEquals(evaluation1.getFeedback(), dtos.get(0).feedback());
        assertEquals(interviewId, dtos.get(0).interviewId());
        assertEquals(evaluationTypeId, dtos.get(0).evaluationTypeId());
        
        assertEquals(evaluation2.getId(), dtos.get(1).id());
        assertEquals(evaluation2.getScore(), dtos.get(1).score());
        assertEquals(evaluation2.getFeedback(), dtos.get(1).feedback());
        assertEquals(interviewId, dtos.get(1).interviewId());
        assertEquals(evaluationTypeId, dtos.get(1).evaluationTypeId());
    }

    @Test
    void fromEvaluationToFullEvaluationDTO_should_return_valid_fullEvaluationDTo(){
        Evaluation evaluation = new Evaluation();
        evaluation.setId(UUID.randomUUID());
        evaluation.setScore(4.5);
        evaluation.setFeedback("Good technical skills");
        Interview interview = new Interview();
        interview.setId(UUID.randomUUID());
        evaluation.setInterview(interview);
        EvaluationType evaluationType = new EvaluationType();
        evaluationType.setId(UUID.randomUUID());
        evaluation.setEvaluationType(evaluationType);
        evaluationType.setDescription("test");
        evaluationType.setCoefficient(1.0);

        evaluation.setEvaluationType(evaluationType);

        EvaluationTypeDTO expectedEvaluationTypeDTO = new EvaluationTypeDTO(
                evaluationType.getId(),
                "Technical Skills",
                1.0
        );
        when(evaluationTypeMapper.toDto(evaluationType)).thenReturn(expectedEvaluationTypeDTO);

        FullEvaluationDTO fullEvaluation = evaluationMapper.fromEvaluationToFullEvaluationDTO(evaluation);

        assertNotNull(fullEvaluation);
        assertEquals(evaluation.getId(), fullEvaluation.id());
        assertEquals(evaluation.getScore(), fullEvaluation.score());
        assertEquals(evaluation.getFeedback(), fullEvaluation.feedback());
        assertEquals(interview.getId(), fullEvaluation.interviewId());
        assertNotNull(fullEvaluation.evaluationType());
        assertEquals(evaluationType.getId(), fullEvaluation.evaluationType().id());
        assertEquals("Technical Skills", fullEvaluation.evaluationType().description());
        assertEquals(1.0, fullEvaluation.evaluationType().coefficient());

    }

    @Test
    void mapAIEvaluationResponsesToEvaluations_should_return_valid_evaluation_list(){
        EvaluationsAIResponseDTO evaluationsAIResponse1 = new EvaluationsAIResponseDTO(60.0,"good","communication");
        EvaluationsAIResponseDTO evaluationsAIResponse2 = new EvaluationsAIResponseDTO(70.0,"perfect","problem solving");
        EvaluationsAIResponseDTO evaluationsAIResponse3 = new EvaluationsAIResponseDTO(80.0,"excellent","react");

        List<EvaluationsAIResponseDTO> aiEvaluationResponses = List.of(evaluationsAIResponse1,evaluationsAIResponse2,evaluationsAIResponse3);

        Evaluation evaluation1 = new Evaluation();
        evaluation1.setId(UUID.randomUUID());
        EvaluationType evaluationType1 = new EvaluationType();
        evaluationType1.setDescription("problem solving");
        evaluation1.setEvaluationType(evaluationType1);

        Evaluation evaluation2 = new Evaluation();
        evaluation2.setId(UUID.randomUUID());
        EvaluationType evaluationType2 = new EvaluationType();
        evaluationType2.setDescription("react");
        evaluation2.setEvaluationType(evaluationType2);

        Evaluation evaluation3 = new Evaluation();
        evaluation3.setId(UUID.randomUUID());
        EvaluationType evaluationType3 = new EvaluationType();
        evaluationType3.setDescription("communication");
        evaluation3.setEvaluationType(evaluationType3);

        List<Evaluation> evaluations = List.of(evaluation1,evaluation2,evaluation3);


        List<Evaluation> res = evaluationMapper.mapAIEvaluationResponsesToEvaluations(aiEvaluationResponses
        , evaluations);

        assertThat(evaluationsAIResponse1.score()).isEqualTo(res.get(2).getScore());
        assertThat(evaluationsAIResponse2.score()).isEqualTo(res.get(0).getScore());
        assertThat(evaluationsAIResponse3.score()).isEqualTo(res.get(1).getScore());

        assertThat(evaluationsAIResponse1.feedback()).isEqualTo(res.get(2).getFeedback());
        assertThat(evaluationsAIResponse2.feedback()).isEqualTo(res.get(0).getFeedback());
        assertThat(evaluationsAIResponse3.feedback()).isEqualTo(res.get(1).getFeedback());


    }

    @Test
    void mapEvaluationToInterviewEvaluation_should_return_valid_interviewEValuationDTO(){
        UUID interviewId = UUID.randomUUID();
        UUID evaluationId1 = UUID.randomUUID();
        UUID evaluationId2 = UUID.randomUUID();
        UUID evaluationTypeId1 = UUID.randomUUID();
        UUID evaluationTypeId2 = UUID.randomUUID();

        Interview interview = new Interview();
        interview.setId(interviewId);
        interview.setScheduledAt(LocalDateTime.of(2024, 1, 15, 10, 0));
        interview.setEstimatedDuration(60);

        Candidate candidate = new Candidate();
        candidate.setFullName("hamid");

        Offer offer = new Offer();
        offer.setTitle("backend developer");
        interview.setCandidate(candidate);
        interview.setOffer(offer);

        EvaluationType evaluationType1 = new EvaluationType();
        evaluationType1.setId(evaluationTypeId1);
        evaluationType1.setDescription("Technical Skills");
        evaluationType1.setCoefficient(1.0);

        EvaluationType evaluationType2 = new EvaluationType();
        evaluationType2.setId(evaluationTypeId2);
        evaluationType2.setDescription("Communication");
        evaluationType2.setCoefficient(0.8);

        Evaluation evaluation1 = new Evaluation();
        evaluation1.setId(evaluationId1);
        evaluation1.setScore(4.5);
        evaluation1.setFeedback("Excellent technical skills");
        evaluation1.setInterview(interview);
        evaluation1.setEvaluationType(evaluationType1);

        Evaluation evaluation2 = new Evaluation();
        evaluation2.setId(evaluationId2);
        evaluation2.setScore(4.0);
        evaluation2.setFeedback("Good communication");
        evaluation2.setInterview(interview);
        evaluation2.setEvaluationType(evaluationType2);

        List<Evaluation> evaluations = List.of(evaluation1, evaluation2);

        EvaluationTypeDTO evaluationTypeDTO1 = new EvaluationTypeDTO(
                evaluationTypeId1, "Technical Skills", 1.0
        );
        EvaluationTypeDTO evaluationTypeDTO2 = new EvaluationTypeDTO(
                evaluationTypeId2, "Communication", 0.8
        );

        when(evaluationTypeMapper.toDto(evaluationType1)).thenReturn(evaluationTypeDTO1);
        when(evaluationTypeMapper.toDto(evaluationType2)).thenReturn(evaluationTypeDTO2);

        InterviewEvaluationDTO result = evaluationMapper.mapEvaluationToInterviewEvaluation(evaluations);

        assertNotNull(result);
        assertThat(result.candidateFullName()).isEqualTo(candidate.getFullName());
        assertThat(result.offerTitle()).isEqualTo(offer.getTitle());
        assertThat(LocalDateTime.of(2024, 1, 15, 10, 0)).isEqualTo( result.scheduledAt());
        assertThat(60).isEqualTo( result.estimatedDuration());

        assertNotNull(result.evaluations());
        assertThat(2).isEqualTo( result.evaluations().size());

        FullEvaluationDTO firstEval = result.evaluations().get(0);
        assertThat(evaluationId1).isEqualTo( firstEval.id());
        assertThat(4.5).isEqualTo( firstEval.score());
        assertThat("Excellent technical skills").isEqualTo( firstEval.feedback());
        assertThat(interviewId).isEqualTo( firstEval.interviewId());
        assertThat(evaluationTypeId1).isEqualTo( firstEval.evaluationType().id());

        FullEvaluationDTO secondEval = result.evaluations().get(1);
        assertThat(evaluationId2).isEqualTo(secondEval.id());
        assertThat(4.0).isEqualTo( secondEval.score());
        assertThat("Good communication").isEqualTo(secondEval.feedback());
        assertThat(interviewId).isEqualTo(secondEval.interviewId());
        assertThat(evaluationTypeId2).isEqualTo( secondEval.evaluationType().id());

    }

    @Test
    void testToEntity_shouldSetInterviewAndEvaluationType() {
        UUID interviewId = UUID.randomUUID();
        UUID evaluationTypeId = UUID.randomUUID();

        EvaluationDTO dto = new EvaluationDTO(
                UUID.randomUUID(),
                5.0,
                "Strong skills",
                interviewId,
                evaluationTypeId
        );

        Evaluation entity = evaluationMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.score(), entity.getScore());
        assertEquals(dto.feedback(), entity.getFeedback());

        assertNotNull(entity.getInterview());
        assertEquals(interviewId, entity.getInterview().getId());

        assertNotNull(entity.getEvaluationType());
        assertEquals(evaluationTypeId, entity.getEvaluationType().getId());
    }

    @Test
    void mapEvaluationToInterviewEvaluation_shouldReturnNull_whenListIsNull() {
        InterviewEvaluationDTO dto = evaluationMapper.mapEvaluationToInterviewEvaluation(null);
        assertNull(dto);
    }

    @Test
    void mapEvaluationToInterviewEvaluation_shouldReturnNull_whenListIsEmpty() {
        InterviewEvaluationDTO dto = evaluationMapper.mapEvaluationToInterviewEvaluation(List.of());
        assertNull(dto);
    }

}