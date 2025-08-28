package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.CreateRecordRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordDTO;
import ma.nttdata.externals.module.interview.entity.Record;
import ma.nttdata.externals.module.interview.mapper.RecordMapper;
import ma.nttdata.externals.module.interview.repository.RecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecordServImplTest {

    @Mock
    private RecordRepository recordRepository;
    @Mock
    private RecordMapper recordMapper;

    private RecordServImpl recordServ;

    @BeforeEach
    public void setUp() {
        recordServ = new RecordServImpl(recordRepository, recordMapper);
    }


    @Test
    void should_create_record(){
        CreateRecordRequestDTO req = new CreateRecordRequestDTO(
                LocalDateTime.now(),"http://loclahost","hello"
        );

        Record record = new Record();
        record.setId(UUID.randomUUID());
        record.setRecordedAt(req.recordedAt());
        record.setTranscript(req.transcript());
        record.setFileUrl(req.fileUrl());

        when(recordMapper.fromCreateRecordRequestDtoToEntity(req)).thenReturn(record);
        when(recordRepository.save(record)).thenReturn(record);

        Record result = recordServ.createRecord(req);

        assertNotNull(result);
        verify(recordMapper).fromCreateRecordRequestDtoToEntity(req);
        verify(recordRepository).save(record);
    }

    @Test
    void should_create_record_and_return_dto(){
        CreateRecordRequestDTO req = new CreateRecordRequestDTO(
                LocalDateTime.now(),"http://loclahost","hello"
        );

        Record record = new Record();
        record.setId(UUID.randomUUID());
        record.setRecordedAt(req.recordedAt());
        record.setTranscript(req.transcript());
        record.setFileUrl(req.fileUrl());

        RecordDTO dto = new RecordDTO(record.getId(), record.getRecordedAt(),
                record.getTranscript(), record.getFileUrl());

        when(recordMapper.fromCreateRecordRequestDtoToEntity(req)).thenReturn(record);
        when(recordRepository.save(record)).thenReturn(record);
        when(recordMapper.toDto(record)).thenReturn(dto);

        RecordDTO result = recordServ.createRecordAndReturnDTO(req);

        assertNotNull(result);
        verify(recordMapper).fromCreateRecordRequestDtoToEntity(req);
        verify(recordRepository).save(record);
        verify(recordMapper).toDto(record);
    }

    @Test
    void should_find_record_by_id(){
        UUID id = UUID.randomUUID();
        Record record = new Record();
        record.setId(id);

        RecordDTO dto = new RecordDTO(id, null, null, null);

        when(recordRepository.findById(id)).thenReturn(Optional.of(record));
        when(recordMapper.toDto(record)).thenReturn(dto);

        RecordDTO result = recordServ.findRecordById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        verify(recordRepository).findById(id);
        verify(recordMapper).toDto(record);
    }

    @Test
    void should_throw_when_record_not_found_by_id(){
        UUID id = UUID.randomUUID();
        when(recordRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recordServ.findRecordById(id));
        verify(recordRepository).findById(id);
    }

    @Test
    void should_find_record_by_interview_id(){
        UUID interviewId = UUID.randomUUID();
        Record record = new Record();
        record.setId(UUID.randomUUID());

        RecordDTO dto = new RecordDTO(record.getId(), null, null, null);

        when(recordRepository.findRecordByInterviewId(interviewId)).thenReturn(record);
        when(recordMapper.toDto(record)).thenReturn(dto);

        RecordDTO result = recordServ.findRecordByInterviewId(interviewId);

        assertNotNull(result);
        verify(recordRepository).findRecordByInterviewId(interviewId);
        verify(recordMapper).toDto(record);
    }

    @Test
    void should_find_all_records(){
        Record record = new Record();
        record.setId(UUID.randomUUID());
        List<Record> list = List.of(record);
        List<RecordDTO> dtoList = List.of(new RecordDTO(record.getId(), null, null, null));

        when(recordRepository.findAll()).thenReturn(list);
        when(recordMapper.toListDto(list)).thenReturn(dtoList);

        List<RecordDTO> result = recordServ.findAllRecords();

        assertEquals(1, result.size());
        verify(recordRepository).findAll();
        verify(recordMapper).toListDto(list);
    }

    @Test
    void should_find_all_records_by_offer_id(){
        UUID offerId = UUID.randomUUID();
        Record record = new Record();
        record.setId(UUID.randomUUID());
        List<Record> list = List.of(record);
        List<RecordDTO> dtoList = List.of(new RecordDTO(record.getId(), null, null, null));

        when(recordRepository.findAllRecordsByOfferId(offerId)).thenReturn(list);
        when(recordMapper.toListDto(list)).thenReturn(dtoList);

        List<RecordDTO> result = recordServ.findAllRecordsByOfferId(offerId);

        assertEquals(1, result.size());
        verify(recordRepository).findAllRecordsByOfferId(offerId);
        verify(recordMapper).toListDto(list);
    }

    @Test
    void should_update_record(){
        UUID id = UUID.randomUUID();
        RecordDTO dto = new RecordDTO(id, LocalDateTime.now(), "transcript", "fileUrl");
        Record record = new Record();
        record.setId(id);

        when(recordRepository.findById(id)).thenReturn(Optional.of(record));
        when(recordRepository.save(record)).thenReturn(record);
        when(recordMapper.toDto(record)).thenReturn(dto);

        RecordDTO result = recordServ.updateRecord(dto);

        assertNotNull(result);
        assertEquals(dto.id(), result.id());
        verify(recordRepository).findById(id);
        verify(recordRepository).save(record);
    }

    @Test
    void should_delete_record_by_id(){
        UUID id = UUID.randomUUID();
        when(recordRepository.existsById(id)).thenReturn(true);

        recordServ.deleteRecordById(id);

        verify(recordRepository).existsById(id);
        verify(recordRepository).deleteById(id);
    }

    @Test
    void should_throw_when_delete_record_by_id_not_found(){
        UUID id = UUID.randomUUID();
        when(recordRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> recordServ.deleteRecordById(id));
        verify(recordRepository).existsById(id);
    }

    @Test
    void should_delete_record_by_dto(){
        UUID id = UUID.randomUUID();
        RecordDTO dto = new RecordDTO(id, null, null, null);
        Record entity = new Record();
        entity.setId(id);

        when(recordRepository.existsById(id)).thenReturn(true);
        when(recordMapper.toEntity(dto)).thenReturn(entity);

        recordServ.deleteRecord(dto);

        verify(recordRepository).existsById(id);
        verify(recordMapper).toEntity(dto);
        verify(recordRepository).delete(entity);
    }

    @Test
    void should_throw_when_delete_record_by_dto_not_found(){
        UUID id = UUID.randomUUID();
        RecordDTO dto = new RecordDTO(id, null, null, null);
        when(recordRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> recordServ.deleteRecord(dto));
        verify(recordRepository).existsById(id);
    }
}
