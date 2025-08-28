package ma.nttdata.externals.module.interview.service.impl;

import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.interview.dto.CreateRecordRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordDTO;
import ma.nttdata.externals.module.interview.entity.Record;
import ma.nttdata.externals.module.interview.mapper.RecordMapper;
import ma.nttdata.externals.module.interview.repository.RecordRepository;
import ma.nttdata.externals.module.interview.service.RecordServ;
import org.springframework.stereotype.Service;

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
}
