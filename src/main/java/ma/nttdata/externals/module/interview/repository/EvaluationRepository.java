package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvaluationRepository extends JpaRepository<Evaluation , UUID> {
}
