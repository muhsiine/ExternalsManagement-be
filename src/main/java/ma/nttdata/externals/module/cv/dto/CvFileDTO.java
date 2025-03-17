package ma.nttdata.externals.module.cv.dto;

import lombok.Builder;


public record CvFileDTO(
        String promptCode,
        String b64EFile,
        String mimeType,
        String extractedData, // json
        String filePath
) {
}
