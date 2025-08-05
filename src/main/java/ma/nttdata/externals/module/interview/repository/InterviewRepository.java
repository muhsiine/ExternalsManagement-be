package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Interview;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, UUID> {
    List<Interview> findByOfferId(UUID offerId);

    /*
     * Retrieves an Interview entity by its ID along with its associated Candidate's contacts
     * and the related Offer entity in a single query.
     */
    @EntityGraph(attributePaths = {
            "candidate.contacts",
            "offer"
    })
    Optional<Interview> findWithCandidateAndOfferById(UUID interviewId);

    @EntityGraph(attributePaths = {
            "candidate",
            "offer"
    })
    Optional<Interview> findWithCandidateWithoutContactsAndOfferById(UUID id);
}