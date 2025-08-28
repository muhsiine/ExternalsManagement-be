package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.CreateRecordRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordDTO;
import ma.nttdata.externals.module.interview.entity.Record;
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
public class RecordMapperTest {

    private final RecordMapper recordMapper = Mappers.getMapper(RecordMapper.class);

    @Test
    void fromCreateRecordRequestDtoToEntity_should_map_to_entity(){
        CreateRecordRequestDTO req = new CreateRecordRequestDTO(
                LocalDateTime.now(),"http://loclahost","hello"
        );

        Record record = recordMapper.fromCreateRecordRequestDtoToEntity(req);

        assertNotNull(record);
        assertThat(req.recordedAt()).isEqualTo(record.getRecordedAt());
        assertThat(req.fileUrl()).isEqualTo(record.getFileUrl());
        assertThat(req.transcript()).isEqualTo(record.getTranscript());
    }

    @Test
    void toDTo_should_map_to_dto(){
        Record record = new Record();
        record.setId(UUID.randomUUID());
        record.setRecordedAt(LocalDateTime.now());
        record.setFileUrl("http://loclahost");
        record.setTranscript("hello");

        RecordDTO dto = recordMapper.toDto(record);

        assertNotNull(dto);
        assertThat(dto.id()).isEqualTo(record.getId());
        assertThat(dto.recordedAt()).isEqualTo(record.getRecordedAt());
        assertThat(dto.fileUrl()).isEqualTo(record.getFileUrl());
        assertThat(dto.transcript()).isEqualTo(record.getTranscript());
    }

    @Test
    void toEntity_should_map_to_entity(){
        RecordDTO dto = new RecordDTO(UUID.randomUUID(),LocalDateTime.now(),
                "http://loclahost","hello");

        Record record = recordMapper.toEntity(dto);

        assertNotNull(record);
        assertThat(dto.id()).isEqualTo(record.getId());
        assertThat(dto.recordedAt()).isEqualTo(record.getRecordedAt());
        assertThat(dto.fileUrl()).isEqualTo(record.getFileUrl());
        assertThat(dto.transcript()).isEqualTo(record.getTranscript());
    }

    @Test
    void toListDto_should_map_list_of_entities_to_list_of_recordDto(){
        Record record1 = new Record();
        record1.setId(UUID.randomUUID());
        record1.setRecordedAt(LocalDateTime.now());
        record1.setFileUrl("http://loclahost");
        record1.setTranscript("hello");

        Record record2 = new Record();
        record2.setId(UUID.randomUUID());
        record2.setRecordedAt(LocalDateTime.now());
        record2.setFileUrl("http://loclahost/33");
        record2.setTranscript("we are testing");

        List<RecordDTO> recordDTOS = recordMapper.toListDto(List.of(record1, record2));


        assertNotNull(recordDTOS);
        assertThat(recordDTOS.size()).isEqualTo(2);
        assertThat(recordDTOS.get(0).id()).isEqualTo(record1.getId());
        assertThat(recordDTOS.get(1).id()).isEqualTo(record2.getId());

        assertThat(recordDTOS.get(0).recordedAt()).isEqualTo(record1.getRecordedAt());
        assertThat(recordDTOS.get(1).recordedAt()).isEqualTo(record2.getRecordedAt());

        assertThat(recordDTOS.get(0).transcript()).isEqualTo(record1.getTranscript());
        assertThat(recordDTOS.get(1).transcript()).isEqualTo(record2.getTranscript());

        assertThat(recordDTOS.get(0).fileUrl()).isEqualTo(record1.getFileUrl());
        assertThat(recordDTOS.get(1).fileUrl()).isEqualTo(record2.getFileUrl());
    }
}
