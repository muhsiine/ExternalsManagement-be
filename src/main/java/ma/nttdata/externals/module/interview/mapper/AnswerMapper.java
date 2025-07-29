package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.AnswerDTO;
import ma.nttdata.externals.module.interview.entity.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);

    // toDto
    @Mapping(target = "id" , source = "id")
    @Mapping(target = "description"  , source = "description")
    @Mapping(target = "durationInMinutes" , source = "durationInMinutes")
    AnswerDTO toDto(Answer answer);

    // toEntity
    @Mapping(target = "id" , source = "id")
    @Mapping(target = "description" , source = "description")
    @Mapping(target = "durationInMinutes" , source = "durationInMinutes")
    Answer toEntity(AnswerDTO answerDTO);

    List <AnswerDTO> toDtoList(List<Answer> entities);

}