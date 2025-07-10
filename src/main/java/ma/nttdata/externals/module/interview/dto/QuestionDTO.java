package ma.nttdata.externals.module.interview.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record QuestionDTO(
        UUID id,
        String description,
        UUID interviewId,
        String type,
        Integer points,
        Integer questionOrder,
        LocalDateTime createdAt,
        List<String> tags
) {}