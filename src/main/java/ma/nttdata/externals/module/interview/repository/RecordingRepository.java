package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Recording;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RecordingRepository extends JpaRepository<Recording, UUID> {

    @Query(value = "select r from Interview i join i.recording r where i.id = :interviewId")
    Recording findRecordByInterviewId(@Param("interviewId")UUID interviewId);

    @Query(value = "select r from Interview i join i.offer o join i.recording r where o.id =:offerId")
    List<Recording> findAllRecordsByOfferId(@Param("offerId")UUID offerId);
}
