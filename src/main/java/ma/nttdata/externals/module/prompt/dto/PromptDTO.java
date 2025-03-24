package ma.nttdata.externals.module.prompt.dto;

import java.util.UUID;

public record PromptDTO(
        UUID id,
        String promptCode,
        String promptDesc
) {}