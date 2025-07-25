package ma.nttdata.externals.module.interview.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record GenerateInterviewLinkDTO(
        UUID candidateId,
        UUID offerId,
        UUID interviewId,
        LocalDateTime scheduledAt
) {
}
