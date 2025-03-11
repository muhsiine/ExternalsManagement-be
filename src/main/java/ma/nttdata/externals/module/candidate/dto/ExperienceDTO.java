package ma.nttdata.externals.module.candidate.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ExperienceDTO(
        UUID id,
        String companyName,
        String position,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {}
