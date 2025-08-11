package ma.nttdata.externals.module.interview.dto;

public record EvaluationsAIResponseDTO(
        Double score,
        String feedback,
        String evaluationType
) {
}
