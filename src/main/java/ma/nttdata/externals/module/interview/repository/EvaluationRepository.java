package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.dto.AnswerDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluationRepository extends JpaRepository<Evaluation , UUID> {

    Optional <Evaluation> findTypeById(UUID evaluationId);

    // it will fetch evaluation and its evaluation type with interviewID
    @EntityGraph(attributePaths = {"evaluationType"})
    List<Evaluation> findByInterviewId(UUID interviewId);

}
