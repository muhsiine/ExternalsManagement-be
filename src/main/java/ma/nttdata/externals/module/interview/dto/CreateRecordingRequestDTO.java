package ma.nttdata.externals.module.interview.dto;

import java.time.LocalDateTime;

public record CreateRecordingRequestDTO(
        LocalDateTime recordedAt,
        String fileUrl,
        String transcript
) { }
