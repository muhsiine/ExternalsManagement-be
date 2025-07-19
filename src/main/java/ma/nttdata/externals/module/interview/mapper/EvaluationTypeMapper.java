package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EvaluationTypeMapper {

    @Mapping(target = "id" , source = "id")
    @Mapping(target = "description" ,source = "description")
    @Mapping(target ="coefficient" , source = "coefficient")
    EvaluationTypeDTO toDto(EvaluationType entity);

}
