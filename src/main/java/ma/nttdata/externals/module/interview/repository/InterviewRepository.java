package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Interview;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, UUID> {
    List<Interview> findByOfferId(UUID offerId);

    @EntityGraph(attributePaths = {
            "candidate.contacts",
            "offer"
    })
    Optional<Interview> getCandidateContactsAndOfferByInterviewId(UUID interviewId);
}