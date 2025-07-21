package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationTypeMapperTest {

    private final EvaluationTypeMapper evaluationTypeMapper = Mappers.getMapper(EvaluationTypeMapper.class);

    @Test
    void testToDto() {
        // Arrange
        UUID typeId = UUID.randomUUID();
        
        EvaluationType evaluationType = new EvaluationType();
        evaluationType.setId(typeId);
        evaluationType.setDescription("Technical Skills");
        evaluationType.setCoefficient(2.5);

        // Act
        EvaluationTypeDTO dto = evaluationTypeMapper.toDto(evaluationType);

        // Assert
        assertNotNull(dto);
        assertEquals(evaluationType.getId(), dto.id());
        assertEquals(evaluationType.getDescription(), dto.description());
        assertEquals(evaluationType.getCoefficient(), dto.coefficient());
    }

    @Test
    void testToEntity() {
        // Arrange
        UUID typeId = UUID.randomUUID();
        
        EvaluationTypeDTO dto = new EvaluationTypeDTO(
                typeId,
                "Communication Skills",
                1.5
        );

        // Act
        EvaluationType entity = evaluationTypeMapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.description(), entity.getDescription());
        assertEquals(dto.coefficient(), entity.getCoefficient());
    }
}