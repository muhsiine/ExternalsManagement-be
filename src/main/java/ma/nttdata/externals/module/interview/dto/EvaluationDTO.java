package ma.nttdata.externals.module.interview.dto;

import java.util.UUID;

public record EvaluationDTO(

        UUID id,
        Double score,
        String feedback,
        UUID interviewId,
        UUID evaluationTypeId


)

{}
