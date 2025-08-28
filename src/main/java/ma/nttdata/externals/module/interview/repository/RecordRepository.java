package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecordRepository extends JpaRepository<Record, UUID> {
}
