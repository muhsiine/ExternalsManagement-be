package ma.nttdata.externals.module.candidate.repository;

import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.entity.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static ma.nttdata.externals.module.candidate.constants.LanguageLevel.ADVANCED;
import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
class CandidateRepositoryTest {

    @Autowired
    private CandidateRepository candidateRepository;

    @Test
    void testFindCandidatesRoughMatch() {
        Candidate candidate = new Candidate();
        candidate.setFullName("Zakariae");
        candidate.setMainTech("Java Spring Boot");
        candidate.setYearsOfExperience(6);

        Language lang = new Language();
        lang.setLanguageInEnglish("English");
        lang.setLevel(ADVANCED);
        lang.setCandidate(candidate);

        lang.setDescription("English language");
        lang.setEnglishDescription("English");
        lang.setFullDescription("Advanced English proficiency");
        lang.setNative(false);
        lang.setLanguage("EN");

        candidate.setLanguages(List.of(lang));

        candidateRepository.save(candidate);

        List<Candidate> result = candidateRepository.findCandidatesRoughMatch("Java", 5, List.of("english"));

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getFullName()).isEqualTo("Zakariae");
    }

}