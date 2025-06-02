package ma.nttdata.externals.module.interview.dto;

import java.util.UUID;

public record InterviewRequestDTO(
    UUID candidateId,
    String difficultyLevel,
    String mainTechnology
) {} 