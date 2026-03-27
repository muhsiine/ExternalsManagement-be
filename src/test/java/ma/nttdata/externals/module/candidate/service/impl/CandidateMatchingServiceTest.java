package ma.nttdata.externals.module.candidate.service.impl;

import ma.nttdata.externals.module.candidate.constants.LanguageLevel;
import ma.nttdata.externals.module.candidate.dto.OfferFormattedDescriptionLanguageDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.entity.Language;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.offer.dto.OfferCandidatesDTO;
import ma.nttdata.externals.module.offer.dto.OfferFormattedDescriptionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class CandidateMatchingServiceTest {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateSrvImpl candidateMatchingService;

    @BeforeEach
    void cleanDb() {
        candidateRepository.deleteAll();
    }

    private Candidate buildCandidate(String name, String tech, int years, String lang, LanguageLevel level) {
        Candidate c = new Candidate();
        c.setFullName(name);
        c.setMainTech(tech);
        c.setYearsOfExperience(years);

        Language l = new Language();
        l.setLanguage(lang);
        l.setLanguageInEnglish(lang);
        l.setLevel(level);
        l.setDescription(lang + " desc");
        l.setFullDescription(lang + " full");
        l.setEnglishDescription(lang);
        l.setCandidate(c);

        c.setLanguages(List.of(l));
        return c;
    }

    @Test
    void testRecommendCandidates_FiltersByYearsAndMainTech() {
        Candidate junior = buildCandidate("Ali", "Java", 2, "English", LanguageLevel.INTERMEDIATE);
        Candidate senior = buildCandidate("Sara", "Java", 6, "English", LanguageLevel.INTERMEDIATE);
        candidateRepository.saveAll(List.of(junior, senior));

        OfferFormattedDescriptionDTO offer = new OfferFormattedDescriptionDTO(
                "desc",
                "Java",
                "Java - Spring",
                List.of(new OfferFormattedDescriptionLanguageDTO("English", LanguageLevel.INTERMEDIATE)),
                5,
                "responsibilities",
                "education",
                "keywords"
        );

        List<OfferCandidatesDTO> result = candidateMatchingService.findRecommendedCandidates(offer);

        assertThat(result)
                .extracting(OfferCandidatesDTO::fullName)
                .containsExactly("Sara");
    }

    @Test
    void testRecommendCandidates_RespectsLanguageLevel() {
        Candidate advanced = buildCandidate("Yassine", "Java", 5, "English", LanguageLevel.ADVANCED);
        Candidate beginner = buildCandidate("Omar", "Java", 5, "English", LanguageLevel.BEGINNER);
        candidateRepository.saveAll(List.of(advanced, beginner));

        OfferFormattedDescriptionDTO offer = new OfferFormattedDescriptionDTO(
                "desc",
                "Java",
                "Java - Spring",
                List.of(new OfferFormattedDescriptionLanguageDTO("English", LanguageLevel.INTERMEDIATE)),
                3,
                "responsibilities",
                "education",
                "keywords"
        );

        List<OfferCandidatesDTO> result = candidateMatchingService.findRecommendedCandidates(offer);

        assertThat(result)
                .extracting(OfferCandidatesDTO::fullName)
                .containsExactly("Yassine");
    }
}
