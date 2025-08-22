package ma.nttdata.externals.module.offer.service.impl;



import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.InternalServerException;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.candidate.constants.LanguageLevel;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.entity.Language;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;

import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.offer.dto.OfferCandidatesDTO;
import ma.nttdata.externals.module.offer.dto.OfferFormattedDescriptionDTO;
import ma.nttdata.externals.module.offer.service.CandidateMatchingServ;
import org.springframework.stereotype.Service;
import ma.nttdata.externals.module.candidate.dto.OfferFormattedDescriptionLanguageDTO;


import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateMatchingServImpl implements CandidateMatchingServ {

    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;

    @Override
    public List<OfferCandidatesDTO> findRecommendedCandidates(OfferFormattedDescriptionDTO offerDTO) {
        List<String> offerLanguageNames = offerDTO.languages() == null ? List.of() :
                offerDTO.languages().stream()
                        .map(l -> l.languageName().toLowerCase(Locale.ROOT).trim())
                        .toList();

        List<Candidate> roughMatches = candidateRepository.findCandidatesRoughMatch(
                offerDTO.mainTech(),
                offerDTO.yearsOfExperience(),
                offerLanguageNames
        );
        List<OfferCandidatesDTO> candidateDTOs = roughMatches.stream()
                .map(candidateMapper::toOfferCandidatesDTO)
                .filter(c -> matchLanguagesWithLevel(c, offerDTO))
                .toList();

        return candidateDTOs;
    }

    private boolean matchLanguagesWithLevel(OfferCandidatesDTO candidateDTO, OfferFormattedDescriptionDTO offerDTO) {
        if (offerDTO.languages() == null || offerDTO.languages().isEmpty()) {
            return true; // no language requirement
        }
        if (candidateDTO.languages() == null || candidateDTO.languages().isEmpty()) {
            return false; // candidate has no languages
        }

        for (var offerLang : offerDTO.languages()) {
            String offerName = offerLang.languageName().toLowerCase(Locale.ROOT).trim();
            LanguageLevel offerLevel = offerLang.level();

            for (var candidateLang : candidateDTO.languages()) {
                String candidateName = candidateLang.languageInEnglish().toLowerCase(Locale.ROOT).trim();
                LanguageLevel candidateLevel = candidateLang.level();

                if (candidateName.equals(offerName)) {
                    if (isLevelSufficient(candidateLevel, offerLevel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isLevelSufficient(LanguageLevel candidateLevel, LanguageLevel offerLevel) {
        // relies on the enum order (BEGINNER < INTERMEDIATE < ADVANCED < NATIVE)
        return candidateLevel.ordinal() >= offerLevel.ordinal();
    }


}
