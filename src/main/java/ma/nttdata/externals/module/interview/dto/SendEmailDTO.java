package ma.nttdata.externals.module.interview.dto;

import java.time.LocalDateTime;

public record SendEmailDTO(
        String candidateFullName,
        String offerTitle,
        String email,
        LocalDateTime scheduledDate,
        String link
) {
}
