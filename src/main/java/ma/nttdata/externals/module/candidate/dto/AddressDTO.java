package ma.nttdata.externals.module.candidate.dto;

import java.util.UUID;

public record AddressDTO(
        UUID id,
        String street,
        String postalCode,
        String fullAddress,
        CityDto city,
        CandidateDTO candidate,
        CountryDto country
) {}
