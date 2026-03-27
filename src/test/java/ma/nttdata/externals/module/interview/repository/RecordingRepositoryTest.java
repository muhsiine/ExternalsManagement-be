package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.entity.Recording;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.offer.entity.Offer;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class RecordingRepositoryTest {

    @Autowired
    private RecordingRepository recordingRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Test
    void shouldFindRecordByInterviewId() {
        Candidate candidate = candidateRepository.save(new Candidate());
        Offer offer = new Offer();
        offer.setTitle("Test Offer");
        offer = offerRepository.save(offer);

        Recording recording = new Recording();
        recording = recordingRepository.save(recording);

        Interview interview = new Interview();
        interview.setCandidate(candidate);
        interview.setOffer(offer);
        interview.setRecording(recording); // ✅ correct side

        interview = interviewRepository.save(interview);

        Recording result =
                recordingRepository.findRecordByInterviewId(interview.getId());

        assertNotNull(result);
    }
}