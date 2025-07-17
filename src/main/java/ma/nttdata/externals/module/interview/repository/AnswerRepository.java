package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
}