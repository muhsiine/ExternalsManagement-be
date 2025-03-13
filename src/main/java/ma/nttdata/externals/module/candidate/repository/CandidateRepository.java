package ma.nttdata.externals.module.candidate.repository;

import ma.nttdata.externals.module.candidate.entity.Candidate;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CandidateRepository extends ListCrudRepository<Candidate, UUID> {
}
