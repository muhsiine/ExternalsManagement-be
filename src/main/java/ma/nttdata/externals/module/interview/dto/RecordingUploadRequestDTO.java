package ma.nttdata.externals.module.interview.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record RecordingUploadRequestDTO(
        UUID interviewId,
        MultipartFile chunk,
        int sequence
) {
}
