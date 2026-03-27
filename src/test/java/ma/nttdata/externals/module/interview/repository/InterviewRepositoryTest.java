package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.offer.entity.Offer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class InterviewRepositoryTest {

    @Autowired
    private InterviewRepository interviewRepository;
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private CandidateRepository candidateRepository;


    @Test
    void shouldFindByOfferId() {
        // Arrange
        Candidate candidate = new Candidate();
        candidate = candidateRepository.save(candidate);

        Offer offer = new Offer();
        offer.setTitle("Test Offer");
        offer = offerRepository.save(offer);

        Interview interview = new Interview();
        interview.setCandidate(candidate);
        interview.setOffer(offer);

        interviewRepository.save(interview);

        // Act
        List<Interview> result =
                interviewRepository.findByOfferId(offer.getId());

        // Assert
        assertFalse(result.isEmpty());
    }
}