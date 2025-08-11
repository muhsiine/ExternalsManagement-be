package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationsAIResponseDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface EvaluationMapper {

    // to Dto
    @Mapping(target = "id", source = "id")
    @Mapping(target = "score", source = "score")
    @Mapping(target = "feedback", source = "feedback")
    @Mapping(target = "interviewId", source = "interview.id")
    @Mapping(target = "evaluationTypeId", source = "evaluationType.id")
    EvaluationDTO toDto(Evaluation evaluation);


    @Mapping(target = "id", source = "id")
    @Mapping(target = "score", source = "score")
    @Mapping(target = "feedback", source = "feedback")
    @Mapping(target = "interview", ignore = true)
    @Mapping(target = "evaluationType", ignore = true)
    Evaluation toEntity(EvaluationDTO dto);

    List<EvaluationDTO> toDtoList(List<Evaluation> list);


    @AfterMapping
    default void setInterviewAndType(EvaluationDTO dto, @MappingTarget Evaluation evaluation) {
        if (dto.interviewId() != null) {
            Interview interview = new Interview();
            interview.setId(dto.interviewId());
            evaluation.setInterview(interview);
        }
        if (dto.evaluationTypeId() != null) {
            EvaluationType type = new EvaluationType();
            type.setId(dto.evaluationTypeId());
            evaluation.setEvaluationType(type);
        }
    }

    default List<Evaluation> mapAIEvaluationResponsesToEvaluations(
            List<EvaluationsAIResponseDTO> aiResponses,
            List<Evaluation> evaluations) {

        for (Evaluation evaluation : evaluations) {
            String evaluationTypeDescription = evaluation.getEvaluationType().getDescription();

            for (EvaluationsAIResponseDTO aiResponse : aiResponses) {
                if (aiResponse.evaluationTypeDescription().equals(evaluationTypeDescription)) {
                    evaluation.setFeedback(aiResponse.feedback());
                    evaluation.setScore(aiResponse.score());
                    break;
                }
            }
        }

        return evaluations;
    }


}
