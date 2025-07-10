package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
}