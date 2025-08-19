package ma.nttdata.externals.module.offer.dto;

import ma.nttdata.externals.module.candidate.dto.OfferFormattedDescriptionLanguageDTO;

import java.util.List;

public record OfferFormattedDescriptionDTO(
        String description,
        String mainTech,
        String skills,
        List<OfferFormattedDescriptionLanguageDTO> languages,
        int yearsOfExperience,
        String mainResponsibilities,
        String education,
        String keywords
) {
}
