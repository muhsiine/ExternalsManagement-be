package ma.nttdata.externals.module.offer.service.impl;



import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.entity.Language;
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
    private final OfferCandidateMapper candidateMapper;

    @Override
    public List<OfferCandidatesDTO> findRecommendedCandidates(OfferFormattedDescriptionDTO offer) {
        List<Candidate> allCandidates = candidateRepository.findAll();

        return allCandidates.stream()
                .filter(candidate -> matchLanguages(candidate, offer))
                .filter(candidate -> matchExperience(candidate, offer))
                .filter(candidate -> matchMainTech(candidate, offer))
                .sorted((c1, c2) -> Integer.compare(
                        c2.getYearsOfExperience() != null ? c2.getYearsOfExperience() : 0,
                        c1.getYearsOfExperience() != null ? c1.getYearsOfExperience() : 0
                ))
                .limit(10)
                .map(candidateMapper::toOfferCandidatesDTO)
                .collect(Collectors.toList());
    }

    // ------------------ HARD GATES ------------------

    private boolean matchLanguages(Candidate candidate, OfferFormattedDescriptionDTO offer) {
        if (offer.languages() == null || offer.languages().isEmpty()) {
            return true;
        }

        List<String> offerLanguages = offer.languages().stream()
                .map(OfferFormattedDescriptionLanguageDTO::languageName)
                .map(lang -> lang.toLowerCase(Locale.ROOT).trim())
                .toList();

        List<String> candidateLanguages = candidate.getLanguages().stream()
                .map(Language::getLanguageInEnglish)
                .map(lang -> lang.toLowerCase(Locale.ROOT).trim())
                .toList();

        return candidateLanguages.containsAll(offerLanguages);
    }

    private boolean matchExperience(Candidate candidate, OfferFormattedDescriptionDTO offer) {
        if (candidate.getYearsOfExperience() == null) {
            return false;
        }
        int candidateYears = candidate.getYearsOfExperience();
        int requiredYears = offer.yearsOfExperience();

        return candidateYears >= requiredYears;
    }

    private boolean matchMainTech(Candidate candidate, OfferFormattedDescriptionDTO offer) {
        if (offer.mainTech() == null || offer.mainTech().isBlank()) {
            return true;
        }
        if (candidate.getMainTech() == null) {
            return false;
        }

        String candidateTech = candidate.getMainTech().toLowerCase(Locale.ROOT);
        String requiredTech = offer.mainTech().toLowerCase(Locale.ROOT);

        return candidateTech.contains(requiredTech);
    }
}
