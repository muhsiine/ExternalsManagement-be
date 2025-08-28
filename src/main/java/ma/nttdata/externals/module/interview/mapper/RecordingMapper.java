package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.CreateRecordingRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordingDTO;
import ma.nttdata.externals.module.interview.entity.Recording;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecordingMapper {

    Recording fromCreateRecordRequestDtoToEntity(CreateRecordingRequestDTO dto);

    RecordingDTO toDto(Recording recording);

    Recording toEntity(RecordingDTO dto);

    List<RecordingDTO> toListDto(List<Recording> recordings);
}
