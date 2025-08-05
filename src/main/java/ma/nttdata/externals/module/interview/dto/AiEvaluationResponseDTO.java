package ma.nttdata.externals.module.interview.dto;

public record AiEvaluationResponseDTO(
        Double score,
        String feedback,
        String evaluationType
) {
}
