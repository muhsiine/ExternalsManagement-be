package ma.nttdata.externals.module.candidate.dto;

import java.util.UUID;

public record ContactDTO(
        UUID id,
        String email,
        String phone
) {}
