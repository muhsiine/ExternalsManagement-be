package ma.nttdata.externals.module.candidate.dto;

import java.util.UUID;

public record CityDto(
        UUID id,
        String name,
        CountryDto country
) {}
