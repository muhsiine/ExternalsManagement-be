package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class EvaluationTypeMapperTest {

    private final EvaluationTypeMapper mapper = Mappers.getMapper(EvaluationTypeMapper.class);

    private EvaluationType evaluationType;
    private EvaluationTypeDTO evaluationTypeDTO;

    @BeforeEach
    void setUp() {
        evaluationType = new EvaluationType();
        evaluationType.setId(UUID.randomUUID());
        evaluationType.setDescription("this is an evaluation type for testing");
        evaluationType.setCoefficient(3.0);

        evaluationTypeDTO = new EvaluationTypeDTO(UUID.randomUUID(),"evaluation type DTO for test"
        ,2.0);
    }

    @Test
    void toDto_should_map_evaluationType_to_evaluationTypeDTO(){
        EvaluationTypeDTO mapped = mapper.toDto(evaluationType);

        assertThat(evaluationType.getId()).isEqualTo(mapped.id());
        assertThat(evaluationType.getDescription()).isEqualTo(mapped.description());
        assertThat(evaluationType.getCoefficient()).isEqualTo(mapped.coefficient());
    }

    @Test
    void toEntity_should_map_correctly_evaluationTypeDto_to_entity(){
        EvaluationType mappedEntity = mapper.toEntity(evaluationTypeDTO);

        assertThat(mappedEntity.getId()).isEqualTo(evaluationTypeDTO.id());
        assertThat(mappedEntity.getDescription()).isEqualTo(evaluationTypeDTO.description());
        assertThat(mappedEntity.getCoefficient()).isEqualTo(evaluationTypeDTO.coefficient());
    }
}
