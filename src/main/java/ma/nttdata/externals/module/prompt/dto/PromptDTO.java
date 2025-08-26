package ma.nttdata.externals.module.prompt.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record PromptDTO(
        UUID id,

        @NotBlank(message = "promptCode cannot be empty")
        String promptCode,

        @NotBlank(message = "promptDesc cannot be empty")
        String promptDesc,

        @NotBlank(message = "schema cannot be empty")
        String schema
) {}