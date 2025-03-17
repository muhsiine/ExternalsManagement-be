package ma.nttdata.externals.module.candidate.dto;

import java.util.List;
import java.util.UUID;

public record CountryDTO(
        UUID id,
        String name,
        String englishName,
        List<CityDTO> cities
) {}
