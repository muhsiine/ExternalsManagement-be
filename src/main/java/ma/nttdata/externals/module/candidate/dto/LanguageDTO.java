package ma.nttdata.externals.module.candidate.dto;

import ma.nttdata.externals.module.candidate.constants.LanguageLevel;

import java.util.UUID;

public record LanguageDTO(
        UUID id,
        CandidateDTO candidateDTO,
        String description,
        String englishDescription,
        String fullDescription,
        String language,
        String languageInEnglish,
        LanguageLevel level,
        boolean isNative
) {}
