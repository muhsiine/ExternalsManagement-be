package ma.nttdata.externals.module.candidate.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String name,
        String email,
        String role,
        LocalDateTime createdAt
) {}
