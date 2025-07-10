package ma.nttdata.externals.module.interview.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewDTO(
        UUID id,
        UUID offerId,
        UUID candidateId,
        LocalDateTime scheduledDate,
        String status
) {}