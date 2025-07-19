package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.dto.AnswerDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluationRepository extends JpaRepository<Evaluation , UUID> {
    List <Evaluation> findByInterviewId(UUID interviewId);
    Optional <Evaluation> findTypeById(UUID evaluationId);
}
