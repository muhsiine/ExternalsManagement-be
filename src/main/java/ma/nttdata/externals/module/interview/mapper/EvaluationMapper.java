package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationsAIResponseDTO;
import ma.nttdata.externals.module.interview.dto.FullEvaluationDTO;
import ma.nttdata.externals.module.interview.dto.InterviewEvaluationDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = { EvaluationTypeMapper.class })
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

    @Mapping(target = "id", source = "id")
    @Mapping(target = "feedback", source = "feedback")
    @Mapping(target = "interviewId", source = "interview.id")
    FullEvaluationDTO fromEvaluationToFullEvaluationDTO(Evaluation evaluation);


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

    default InterviewEvaluationDTO mapEvaluationToInterviewEvaluation(
            List<Evaluation> evaluations
    ) {
        if (evaluations == null || evaluations.isEmpty()) {
            return null;
        }

        UUID interviewId = evaluations.get(0).getInterview().getId();
        String candidateFullName = evaluations.get(0).getInterview().getCandidate().getFullName();
        String offerTitle = evaluations.get(0).getInterview().getOffer().getTitle();
        LocalDateTime scheduledAt = evaluations.get(0).getInterview().getScheduledAt();
        int estimatedDuration = evaluations.get(0).getInterview().getEstimatedDuration();

        List<FullEvaluationDTO> fullEvaluationDTOs =
                evaluations.stream()
                        .map(this::fromEvaluationToFullEvaluationDTO)
                        .toList();

        InterviewEvaluationDTO interviewEvaluation =
                new InterviewEvaluationDTO(
                        interviewId,
                        candidateFullName,
                        offerTitle,
                        scheduledAt,
                        estimatedDuration,
                        fullEvaluationDTOs
                );


        return interviewEvaluation;
    }


}
