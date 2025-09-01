package ma.nttdata.externals.module.interview.dto;


import java.util.UUID;

public record RecordingUploadRequestDTO(
        UUID interviewId,
        int sequence
) {
}
