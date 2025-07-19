package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByInterviewId(UUID interviewId);
}