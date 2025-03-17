package ma.nttdata.externals.module.candidate.dto;

import java.util.UUID;

public record CityDTO(
        UUID id,
        String name,
        CountryDTO country
) {}
