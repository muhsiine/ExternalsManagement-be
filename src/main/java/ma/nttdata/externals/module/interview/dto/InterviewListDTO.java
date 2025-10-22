package ma.nttdata.externals.module.interview.dto;

import java.time.LocalDateTime;
import java.util.UUID;


public record InterviewListDTO(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description ,
        String link  ,
        LocalDateTime scheduledAt ,
        String comment,
        String candidateFullName,
        String candidateMainTech,
        String offerTitle
) {
}
