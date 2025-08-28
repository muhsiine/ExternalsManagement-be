package ma.nttdata.externals.module.interview.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecordDTO(
        UUID id,
        LocalDateTime recordedAt,
        String fileUrl,
        String transcript
) {
}
