package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.interview.entity.Interview;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationMapperTest {

    private final EvaluationMapper evaluationMapper = Mappers.getMapper(EvaluationMapper.class);

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
    void testToEntity() {
        // Arrange
        UUID evaluationId = UUID.randomUUID();
        UUID interviewId = UUID.randomUUID();
        UUID evaluationTypeId = UUID.randomUUID();
        
        EvaluationDTO dto = new EvaluationDTO(
                evaluationId,
                4.5,
                "Good communication skills",
                interviewId,
                evaluationTypeId
        );

        // Act
        Evaluation entity = evaluationMapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.score(), entity.getScore());
        assertEquals(dto.feedback(), entity.getFeedback());
        // Interview and EvaluationType are ignored in the initial mapping
        assertNull(entity.getInterview());
        assertNull(entity.getEvaluationType());
        
        // Test the AfterMapping method
        evaluationMapper.setInterviewAndType(dto, entity);
        assertNotNull(entity.getInterview());
        assertEquals(interviewId, entity.getInterview().getId());
        assertNotNull(entity.getEvaluationType());
        assertEquals(evaluationTypeId, entity.getEvaluationType().getId());
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
}