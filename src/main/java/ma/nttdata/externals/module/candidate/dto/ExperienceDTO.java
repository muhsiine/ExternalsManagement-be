package ma.nttdata.externals.module.candidate.dto;

import java.util.UUID;

public record ExperienceDTO(
        UUID id,
        String companyName,
        String position,
        String startDate,
        String endDate,
        String description
) {}
