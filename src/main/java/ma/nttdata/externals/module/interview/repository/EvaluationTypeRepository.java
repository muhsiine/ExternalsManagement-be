package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvaluationTypeRepository extends JpaRepository<EvaluationType, UUID> {

}
