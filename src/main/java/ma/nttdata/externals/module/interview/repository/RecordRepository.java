package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RecordRepository extends JpaRepository<Record, UUID> {

    @Query(value = "select r from Interview i join i.record r where i.id = :interviewId")
    Record findRecordByInterviewId(@Param("interviewId")UUID interviewId);

    @Query(value = "select r from Interview i join i.offer o join i.record r where o.id =:offerId")
    List<Record> findAllRecordsByOfferId(@Param("offerId")UUID offerId);
}
