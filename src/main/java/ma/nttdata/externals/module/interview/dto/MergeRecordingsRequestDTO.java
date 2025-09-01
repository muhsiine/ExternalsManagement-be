package ma.nttdata.externals.module.interview.dto;

import java.util.UUID;

public record MergeRecordingsRequestDTO(
        UUID interviewId,
        String transcript
) {
}
