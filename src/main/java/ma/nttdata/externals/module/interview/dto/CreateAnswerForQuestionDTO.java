package ma.nttdata.externals.module.interview.dto;

import java.util.UUID;

public record CreateAnswerForQuestionDTO(
        UUID questionId,
        String description,
        Integer durationInMinutes
) {
}