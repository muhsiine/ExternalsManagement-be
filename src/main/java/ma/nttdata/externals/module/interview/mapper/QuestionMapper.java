package ma.nttdata.externals.module.interview.mapper;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

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
    @Mapping(target = "interview" , source = "interviewId")
    @Mapping(target = "answer" , ignore = true)
    Question toEntity(QuestionDTO questionDTO);

    default Interview map(UUID interviewId) {
        if (interviewId == null) return null;
        Interview interview = new Interview();
        interview.setId(interviewId);
        return interview;
    }

    List<QuestionDTO> toDtoList(List<Question> questions);
}
