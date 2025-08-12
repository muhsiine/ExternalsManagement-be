package ma.nttdata.externals.module.interview.dto;

import java.util.UUID;

public record FullEvaluationDTO(
        UUID id,
        Double score,
        String feedback,
        UUID interviewId,
        EvaluationTypeDTO evaluationType
) {
}
