package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.entity.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.offer.entity.Offer;

import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

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

        Question question = new Question();
        question.setInterview(interview);

        questionRepository.save(question);

        List<Question> result =
                questionRepository.findByInterviewId(interview.getId());

        assertFalse(result.isEmpty());
    }
}