package ma.nttdata.externals.module.offer.dto;

import java.util.UUID;

public record OfferDTO(
        UUID id,
        String title,
        String description

) {}