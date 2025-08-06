package ma.nttdata.externals.module.interview.dto;


import java.util.UUID;

public record EvaluationWithInterviewAndEvaluationTypeDTO(
        UUID id,
        Double score,
        String feedback,
        InterviewWithCandidateAndOfferDTO interview,
        EvaluationTypeDTO evaluationType

) {
}
