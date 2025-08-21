package ma.nttdata.externals.module.offer.service.impl;



import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.InternalServerException;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.candidate.constants.LanguageLevel;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.entity.Language;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.offer.mapper.OfferCandidateMapper;
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
    public List<OfferCandidatesDTO> findRecommendedCandidates(OfferFormattedDescriptionDTO offer) {
        List<OfferCandidatesDTO> allCandidates = this.getAllCandidates();
        System.out.println("Loaded candidates: " + allCandidates.size());


        List<OfferCandidatesDTO> filteredCandidates = allCandidates.stream()
                .filter(candidate -> matchLanguages(candidate, offer))
                .filter(candidate -> matchYearsOfExperience(candidate, offer))
                .filter(candidate -> matchMainTech(candidate, offer))
                .toList();

        System.out.println("Candidates after language filter: " + filteredCandidates.size());

        return filteredCandidates;
    }

    // ------------------ HARD GATES ------------------

    private boolean matchLanguages(OfferCandidatesDTO candidateDTO, OfferFormattedDescriptionDTO offerDTO) {
        if (offerDTO.languages() == null || offerDTO.languages().isEmpty()) {
            return true;
        }
        if (candidateDTO.languages() == null || candidateDTO.languages().isEmpty()) {
            return false;
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

    private boolean matchYearsOfExperience(OfferCandidatesDTO candidateDTO, OfferFormattedDescriptionDTO offerDTO) {
        if (offerDTO.yearsOfExperience() == 0) {
            return true;
        }
        if (candidateDTO.yearsOfExperience() == null) {
            return false;
        }

        int required = offerDTO.yearsOfExperience();
        int candidateExp = candidateDTO.yearsOfExperience();


        boolean match = candidateExp >= required;

        return match;
    }

    private boolean matchMainTech(OfferCandidatesDTO candidateDTO, OfferFormattedDescriptionDTO offerDTO) {
        if (offerDTO.mainTech() == null || offerDTO.mainTech().isBlank()) {
            return true;
        }
        if (candidateDTO.mainTech() == null || candidateDTO.mainTech().isBlank()) {
            return false;
        }

        String candidateTech = candidateDTO.mainTech().toLowerCase(Locale.ROOT).trim();
        String offerTech = offerDTO.mainTech().toLowerCase(Locale.ROOT).trim();


        boolean match = candidateTech.equals(offerTech) ||
                candidateTech.contains(offerTech) ||
                offerTech.contains(candidateTech);

        return match;
    }


    private List<OfferCandidatesDTO> getAllCandidates() {
        try {
            List<Candidate> candidates = candidateRepository.findAll();

            if (candidates.isEmpty()) {
                throw new ResourceNotFoundException("No candidates found");
            }

            return candidates.stream()
                    .map(candidateMapper::toOfferCandidatesDTO)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error retrieving candidates", e);
        }
    }
}
