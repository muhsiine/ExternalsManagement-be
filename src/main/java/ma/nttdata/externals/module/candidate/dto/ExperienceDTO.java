package ma.nttdata.externals.module.candidate.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ExperienceDTO(
        UUID id,
        String companyName,
        String position,
        String startDate,
        String endDate,
        String description
) {}
