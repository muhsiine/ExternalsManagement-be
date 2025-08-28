package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.CreateRecordingRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordingDTO;
import ma.nttdata.externals.module.interview.entity.Recording;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class RecordingMapperTest {

    private final RecordingMapper recordingMapper = Mappers.getMapper(RecordingMapper.class);

    @Test
    void fromCreateRecordRequestDtoToEntity_should_map_to_entity(){
        CreateRecordingRequestDTO req = new CreateRecordingRequestDTO(
                LocalDateTime.now(),"http://loclahost","hello"
        );

        Recording record = recordingMapper.fromCreateRecordRequestDtoToEntity(req);

        assertNotNull(record);
        assertThat(req.recordedAt()).isEqualTo(record.getRecordedAt());
        assertThat(req.fileUrl()).isEqualTo(record.getFileUrl());
        assertThat(req.transcript()).isEqualTo(record.getTranscript());
    }

    @Test
    void toDTo_should_map_to_dto(){
        Recording record = new Recording();
        record.setId(UUID.randomUUID());
        record.setRecordedAt(LocalDateTime.now());
        record.setFileUrl("http://loclahost");
        record.setTranscript("hello");

        RecordingDTO dto = recordingMapper.toDto(record);

        assertNotNull(dto);
        assertThat(dto.id()).isEqualTo(record.getId());
        assertThat(dto.recordedAt()).isEqualTo(record.getRecordedAt());
        assertThat(dto.fileUrl()).isEqualTo(record.getFileUrl());
        assertThat(dto.transcript()).isEqualTo(record.getTranscript());
    }

    @Test
    void toEntity_should_map_to_entity(){
        RecordingDTO dto = new RecordingDTO(UUID.randomUUID(),LocalDateTime.now(),
                "http://loclahost","hello");

        Recording record = recordingMapper.toEntity(dto);

        assertNotNull(record);
        assertThat(dto.id()).isEqualTo(record.getId());
        assertThat(dto.recordedAt()).isEqualTo(record.getRecordedAt());
        assertThat(dto.fileUrl()).isEqualTo(record.getFileUrl());
        assertThat(dto.transcript()).isEqualTo(record.getTranscript());
    }

    @Test
    void toListDto_should_map_list_of_entities_to_list_of_recordDto(){
        Recording record1 = new Recording();
        record1.setId(UUID.randomUUID());
        record1.setRecordedAt(LocalDateTime.now());
        record1.setFileUrl("http://loclahost");
        record1.setTranscript("hello");

        Recording record2 = new Recording();
        record2.setId(UUID.randomUUID());
        record2.setRecordedAt(LocalDateTime.now());
        record2.setFileUrl("http://loclahost/33");
        record2.setTranscript("we are testing");

        List<RecordingDTO> recordingDTOS = recordingMapper.toListDto(List.of(record1, record2));


        assertNotNull(recordingDTOS);
        assertThat(recordingDTOS.size()).isEqualTo(2);
        assertThat(recordingDTOS.get(0).id()).isEqualTo(record1.getId());
        assertThat(recordingDTOS.get(1).id()).isEqualTo(record2.getId());

        assertThat(recordingDTOS.get(0).recordedAt()).isEqualTo(record1.getRecordedAt());
        assertThat(recordingDTOS.get(1).recordedAt()).isEqualTo(record2.getRecordedAt());

        assertThat(recordingDTOS.get(0).transcript()).isEqualTo(record1.getTranscript());
        assertThat(recordingDTOS.get(1).transcript()).isEqualTo(record2.getTranscript());

        assertThat(recordingDTOS.get(0).fileUrl()).isEqualTo(record1.getFileUrl());
        assertThat(recordingDTOS.get(1).fileUrl()).isEqualTo(record2.getFileUrl());
    }
}
