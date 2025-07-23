package ma.nttdata.externals.module.interview.dto;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public record InterviewDTO(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description ,
        String link  ,
        String feedback_general ,
        Date scheduledAt ,
        String comment,
        UUID offerId,
        UUID candidateId
) {}