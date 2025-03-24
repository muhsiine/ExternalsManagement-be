package ma.nttdata.externals.module.candidate.repository;

import ma.nttdata.externals.module.candidate.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, UUID> {
    List<Candidate> findByMainTech(String mainTech);
    List<Candidate> findByYearsOfExperienceGreaterThanEqual(Integer yearsOfExperience);
}