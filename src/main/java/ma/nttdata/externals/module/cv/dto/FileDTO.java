package ma.nttdata.externals.module.cv.dto;

import lombok.Builder;

@Builder
public record FileDTO(
        String text,
        String b64EFile,
        String schema
) {
}
