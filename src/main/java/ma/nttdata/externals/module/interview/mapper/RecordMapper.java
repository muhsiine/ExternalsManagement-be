package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.CreateRecordRequestDTO;
import ma.nttdata.externals.module.interview.entity.Record;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecordMapper {

    Record fromCreateRecordRequestDtoToEntity(CreateRecordRequestDTO dto);
}
