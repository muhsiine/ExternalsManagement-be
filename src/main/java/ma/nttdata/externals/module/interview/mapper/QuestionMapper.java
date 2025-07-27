package ma.nttdata.externals.module.interview.mapper;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper{

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    //to dto
    @Mapping(target = "id" , source ="id")
    @Mapping(target = "description"  , source = "description")
    @Mapping(target = "durationInMinutes" , source = "durationInMinutes")
    @Mapping(target = "interviewId" , source = "interview.id")
    @Mapping(target = "answerId" , source = "answer.id")
    QuestionDTO toDto(Question question);


    // to entity
    @Mapping(target = "id" , source = "id")
    @Mapping(target = "description"  , source = "description")
    @Mapping(target = "durationInMinutes" , source = "durationInMinutes")
    @Mapping(target = "interview" , ignore = true)
    @Mapping(target = "answer" , ignore = true)
    Question toEntity(QuestionDTO questionDTO);

    List<QuestionDTO> toDtoList(List<Question> questions);
}
