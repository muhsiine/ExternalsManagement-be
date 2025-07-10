package ma.nttdata.externals.module.interview.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ResponseDTO(
        UUID id,
        String description,
        UUID questionId,
        LocalDateTime createdAt,
        Boolean isCorrect,
        Float score
) {}