package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.AnswerDTO;
import ma.nttdata.externals.module.interview.entity.Answer;
import ma.nttdata.externals.module.interview.entity.Question;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class AnswerMapperTest {

    private final AnswerMapper answerMapper = Mappers.getMapper(AnswerMapper.class);

    @Test
    void testToDto() {
        // Arrange
        UUID answerId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        
        Question question = new Question();
        question.setId(questionId);
        
        Answer answer = new Answer();
        answer.setId(answerId);
        answer.setDescription("This is a test answer");
        answer.setDurationInMinutes(15);

        // Act
        AnswerDTO dto = answerMapper.toDto(answer);

        // Assert
        assertNotNull(dto);
        assertEquals(answer.getId(), dto.id());
        assertEquals(answer.getDescription(), dto.description());
        assertEquals(answer.getDurationInMinutes(), dto.durationInMinutes());
    }

    @Test
    void testToEntity() {
        // Arrange
        UUID answerId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        
        AnswerDTO dto = new AnswerDTO(
                answerId,
                "This is a test answer",
                15
        );

        // Act
        Answer entity = answerMapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.description(), entity.getDescription());
        assertEquals(dto.durationInMinutes(), entity.getDurationInMinutes());
        // Question is ignored in the mapper, so it should be null

    }
    
    @Test
    void testToDtoList() {
        // Arrange
        UUID answerId1 = UUID.randomUUID();
        UUID answerId2 = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        
        Question question = new Question();
        question.setId(questionId);
        
        Answer answer1 = new Answer();
        answer1.setId(answerId1);
        answer1.setDescription("Answer 1");
        answer1.setDurationInMinutes(10);
        
        Answer answer2 = new Answer();
        answer2.setId(answerId2);
        answer2.setDescription("Answer 2");
        answer2.setDurationInMinutes(20);
        
        List<Answer> answers = new ArrayList<>();
        answers.add(answer1);
        answers.add(answer2);

        // Act
        List<AnswerDTO> dtos = answerMapper.toDtoList(answers);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(answer1.getId(), dtos.get(0).id());
        assertEquals(answer1.getDescription(), dtos.get(0).description());
        assertEquals(answer1.getDurationInMinutes(), dtos.get(0).durationInMinutes());
        
        assertEquals(answer2.getId(), dtos.get(1).id());
        assertEquals(answer2.getDescription(), dtos.get(1).description());
        assertEquals(answer2.getDurationInMinutes(), dtos.get(1).durationInMinutes());
    }
}