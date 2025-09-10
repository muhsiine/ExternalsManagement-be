package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.CreateRecordingRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordingDTO;
import ma.nttdata.externals.module.interview.entity.Recording;
import ma.nttdata.externals.module.interview.mapper.RecordingMapper;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
import ma.nttdata.externals.module.interview.repository.RecordingRepository;
import ma.nttdata.externals.module.interview.service.InterviewServ;
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
public class RecordingServImplTest {

    @Mock
    private RecordingRepository recordingRepository;
    @Mock
    private RecordingMapper recordingMapper;
    @Mock
    private InterviewServ interviewServ;

    private RecordingServImpl recordServ;

    @BeforeEach
    public void setUp() {
        recordServ = new RecordingServImpl(recordingRepository, recordingMapper,interviewServ);
    }


    @Test
    void should_create_record(){
        CreateRecordingRequestDTO req = new CreateRecordingRequestDTO(
                LocalDateTime.now(),"http://loclahost","hello"
        );

        Recording record = new Recording();
        record.setId(UUID.randomUUID());
        record.setRecordedAt(req.recordedAt());
        record.setTranscript(req.transcript());
        record.setFileUrl(req.fileUrl());

        when(recordingMapper.fromCreateRecordRequestDtoToEntity(req)).thenReturn(record);
        when(recordingRepository.save(record)).thenReturn(record);

        Recording result = recordServ.createRecording(req);

        assertNotNull(result);
        verify(recordingMapper).fromCreateRecordRequestDtoToEntity(req);
        verify(recordingRepository).save(record);
    }

    @Test
    void should_create_record_and_return_dto(){
        CreateRecordingRequestDTO req = new CreateRecordingRequestDTO(
                LocalDateTime.now(),"http://loclahost","hello"
        );

        Recording record = new Recording();
        record.setId(UUID.randomUUID());
        record.setRecordedAt(req.recordedAt());
        record.setTranscript(req.transcript());
        record.setFileUrl(req.fileUrl());

        RecordingDTO dto = new RecordingDTO(record.getId(), record.getRecordedAt(),
                record.getTranscript(), record.getFileUrl());

        when(recordingMapper.fromCreateRecordRequestDtoToEntity(req)).thenReturn(record);
        when(recordingRepository.save(record)).thenReturn(record);
        when(recordingMapper.toDto(record)).thenReturn(dto);

        RecordingDTO result = recordServ.createRecordingAndReturnDTO(req);

        assertNotNull(result);
        verify(recordingMapper).fromCreateRecordRequestDtoToEntity(req);
        verify(recordingRepository).save(record);
        verify(recordingMapper).toDto(record);
    }

    @Test
    void should_find_record_by_id(){
        UUID id = UUID.randomUUID();
        Recording record = new Recording();
        record.setId(id);

        RecordingDTO dto = new RecordingDTO(id, null, null, null);

        when(recordingRepository.findById(id)).thenReturn(Optional.of(record));
        when(recordingMapper.toDto(record)).thenReturn(dto);

        RecordingDTO result = recordServ.findRecordingById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        verify(recordingRepository).findById(id);
        verify(recordingMapper).toDto(record);
    }

    @Test
    void should_throw_when_record_not_found_by_id(){
        UUID id = UUID.randomUUID();
        when(recordingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recordServ.findRecordingById(id));
        verify(recordingRepository).findById(id);
    }

    @Test
    void should_find_record_by_interview_id(){
        UUID interviewId = UUID.randomUUID();
        Recording record = new Recording();
        record.setId(UUID.randomUUID());

        RecordingDTO dto = new RecordingDTO(record.getId(), null, null, null);

        when(recordingRepository.findRecordByInterviewId(interviewId)).thenReturn(record);
        when(recordingMapper.toDto(record)).thenReturn(dto);

        RecordingDTO result = recordServ.findRecordingByInterviewId(interviewId);

        assertNotNull(result);
        verify(recordingRepository).findRecordByInterviewId(interviewId);
        verify(recordingMapper).toDto(record);
    }

    @Test
    void should_find_all_records(){
        Recording record = new Recording();
        record.setId(UUID.randomUUID());
        List<Recording> list = List.of(record);
        List<RecordingDTO> dtoList = List.of(new RecordingDTO(record.getId(), null, null, null));

        when(recordingRepository.findAll()).thenReturn(list);
        when(recordingMapper.toListDto(list)).thenReturn(dtoList);

        List<RecordingDTO> result = recordServ.findAllRecordings();

        assertEquals(1, result.size());
        verify(recordingRepository).findAll();
        verify(recordingMapper).toListDto(list);
    }

    @Test
    void should_find_all_records_by_offer_id(){
        UUID offerId = UUID.randomUUID();
        Recording record = new Recording();
        record.setId(UUID.randomUUID());
        List<Recording> list = List.of(record);
        List<RecordingDTO> dtoList = List.of(new RecordingDTO(record.getId(), null, null, null));

        when(recordingRepository.findAllRecordsByOfferId(offerId)).thenReturn(list);
        when(recordingMapper.toListDto(list)).thenReturn(dtoList);

        List<RecordingDTO> result = recordServ.findAllRecordingsByOfferId(offerId);

        assertEquals(1, result.size());
        verify(recordingRepository).findAllRecordsByOfferId(offerId);
        verify(recordingMapper).toListDto(list);
    }

    @Test
    void should_update_record(){
        UUID id = UUID.randomUUID();
        RecordingDTO dto = new RecordingDTO(id, LocalDateTime.now(), "transcript", "fileUrl");
        Recording record = new Recording();
        record.setId(id);

        when(recordingRepository.findById(id)).thenReturn(Optional.of(record));
        when(recordingRepository.save(record)).thenReturn(record);
        when(recordingMapper.toDto(record)).thenReturn(dto);

        RecordingDTO result = recordServ.updateRecording(dto);

        assertNotNull(result);
        assertEquals(dto.id(), result.id());
        verify(recordingRepository).findById(id);
        verify(recordingRepository).save(record);
    }

    @Test
    void should_delete_record_by_id(){
        UUID id = UUID.randomUUID();
        when(recordingRepository.existsById(id)).thenReturn(true);

        recordServ.deleteRecordingById(id);

        verify(recordingRepository).existsById(id);
        verify(recordingRepository).deleteById(id);
    }

    @Test
    void should_throw_when_delete_record_by_id_not_found(){
        UUID id = UUID.randomUUID();
        when(recordingRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> recordServ.deleteRecordingById(id));
        verify(recordingRepository).existsById(id);
    }

    @Test
    void should_delete_record_by_dto(){
        UUID id = UUID.randomUUID();
        RecordingDTO dto = new RecordingDTO(id, null, null, null);
        Recording entity = new Recording();
        entity.setId(id);

        when(recordingRepository.existsById(id)).thenReturn(true);
        when(recordingMapper.toEntity(dto)).thenReturn(entity);

        recordServ.deleteRecording(dto);

        verify(recordingRepository).existsById(id);
        verify(recordingMapper).toEntity(dto);
        verify(recordingRepository).delete(entity);
    }

    @Test
    void should_throw_when_delete_record_by_dto_not_found(){
        UUID id = UUID.randomUUID();
        RecordingDTO dto = new RecordingDTO(id, null, null, null);
        when(recordingRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> recordServ.deleteRecording(dto));
        verify(recordingRepository).existsById(id);
    }
}
