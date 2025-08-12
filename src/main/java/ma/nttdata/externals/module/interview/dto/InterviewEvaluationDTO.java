package ma.nttdata.externals.module.interview.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InterviewEvaluationDTO(
        UUID interviewId,
        String candidateFullName,
        String offerTitle,
        LocalDateTime scheduledAt,
        int estimatedDuration,
        List<FullEvaluationDTO> evaluations
) {
}
