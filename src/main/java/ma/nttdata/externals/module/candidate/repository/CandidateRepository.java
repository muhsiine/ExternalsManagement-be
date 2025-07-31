package ma.nttdata.externals.module.candidate.repository;

import ma.nttdata.externals.module.candidate.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    @Query("SELECT DISTINCT c.mainTech FROM Candidate c WHERE c.mainTech IS NOT NULL")
    List<String> findDistinctMainTechs();
}
