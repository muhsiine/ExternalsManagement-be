package ma.nttdata.externals.module.candidate.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

public record CvFileDTO(
        UUID id,
        String fileName,
        String fileType,
        LocalDateTime uploadedAt,
        String fileContent // Base64 encoded file content as a String
) {}
