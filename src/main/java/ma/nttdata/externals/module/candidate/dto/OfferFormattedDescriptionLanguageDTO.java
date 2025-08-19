package ma.nttdata.externals.module.candidate.dto;

import ma.nttdata.externals.module.candidate.constants.LanguageLevel;

public record OfferFormattedDescriptionLanguageDTO(
        String languageName,
        LanguageLevel level
) {
}
