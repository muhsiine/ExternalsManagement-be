package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.CreateRecordRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordDTO;
import ma.nttdata.externals.module.interview.entity.Record;

import java.util.List;
import java.util.UUID;

public interface RecordServ {
    Record createRecord(CreateRecordRequestDTO request);

    RecordDTO createRecordAndReturnDTO(CreateRecordRequestDTO request);

    RecordDTO findRecordById(UUID id);

    RecordDTO findRecordByInterviewId(UUID interviewId);

    List<RecordDTO> findAllRecords();

    List<RecordDTO> findAllRecordsByOfferId(UUID offerId);
}
