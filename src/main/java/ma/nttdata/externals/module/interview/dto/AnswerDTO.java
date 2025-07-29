package ma.nttdata.externals.module.interview.dto;

import java.util.UUID;

public record AnswerDTO(
        UUID id,
        String description,
        Integer durationInMinutes
) {}