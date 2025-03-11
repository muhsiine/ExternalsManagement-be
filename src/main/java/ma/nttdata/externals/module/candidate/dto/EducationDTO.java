package ma.nttdata.externals.module.candidate.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EducationDTO(
        UUID id,
        String institution,
        String degree,
        LocalDate startDate,
        LocalDate endDate
) {}
