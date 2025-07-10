package ma.nttdata.externals.module.interview.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OfferDTO(
        UUID id,
        String titre,
        String description,
        LocalDateTime createdAt,
        String status,
        String type,
        String department


) {}