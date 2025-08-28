package ma.nttdata.externals.module.interview.service.impl;

import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.CreateRecordRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordDTO;
import ma.nttdata.externals.module.interview.entity.Record;
import ma.nttdata.externals.module.interview.mapper.RecordMapper;
import ma.nttdata.externals.module.interview.repository.RecordRepository;
import ma.nttdata.externals.module.interview.service.RecordServ;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecordServImpl implements RecordServ {

    private final RecordRepository recordRepository;
    private final RecordMapper recordMapper;

    @Override
    public Record createRecord(CreateRecordRequestDTO request) {
        Record record = recordMapper.fromCreateRecordRequestDtoToEntity(request);
        return recordRepository.save(record);
    }

    @Override
    public RecordDTO createRecordAndReturnDTO(CreateRecordRequestDTO request) {
        Record record = recordMapper.fromCreateRecordRequestDtoToEntity(request);
        return recordMapper.toDto(recordRepository.save(record));
    }

    @Override
    public RecordDTO findRecordById(UUID id) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record",id));
        return recordMapper.toDto(record);
    }

    @Override
    public RecordDTO findRecordByInterviewId(UUID interviewId) {
        Record record = recordRepository.findRecordByInterviewId(interviewId);
        return recordMapper.toDto(record);
    }

    @Override
    public List<RecordDTO> findAllRecords() {
        List<Record> records = recordRepository.findAll();
        return recordMapper.toListDto(records);
    }

    @Override
    public List<RecordDTO> findAllRecordsByOfferId(UUID offerId) {
        List<Record> records = recordRepository.findAllRecordsByOfferId(offerId);
        return recordMapper.toListDto(records);
    }
}
