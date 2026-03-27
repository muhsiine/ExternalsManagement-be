package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.offer.entity.Offer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import ma.nttdata.externals.module.interview.repository.EvaluationTypeRepository;

import java.util.List;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class EvaluationRepositoryTest {

    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private EvaluationTypeRepository evaluationTypeRepository;
    @Autowired
    private InterviewRepository interviewRepository;
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private OfferRepository offerRepository;


    @Test
    void shouldFindByInterviewId() {
        Candidate candidate = candidateRepository.save(new Candidate());
        Offer offer = new Offer();
        offer.setTitle("Test Offer");
        offer = offerRepository.save(offer);
        Interview interview = new Interview();
        interview.setCandidate(candidate);
        interview.setOffer(offer);
        interview = interviewRepository.save(interview);
        EvaluationType type = new EvaluationType();
        type = evaluationTypeRepository.save(type);

        Evaluation evaluation = new Evaluation();
        evaluation.setInterview(interview);
        evaluation.setEvaluationType(type);
        evaluationRepository.save(evaluation);
        List<Evaluation> result =
                evaluationRepository.findByInterviewId(interview.getId());

        assertFalse(result.isEmpty());
    }
}