package ma.nttdata.externals.module.candidate.dto;

import java.util.List;
import java.util.UUID;

public record CountryDto(
        UUID id,
        String name,
        String englishName,
        List<CityDto> cities
) {}
