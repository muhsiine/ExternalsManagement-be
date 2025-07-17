package ma.nttdata.externals.module.interview.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OfferDTO(
        UUID id,
        String title,
        String description

) {}