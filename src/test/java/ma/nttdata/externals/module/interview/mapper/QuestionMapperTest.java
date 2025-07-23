package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.entity.Question;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuestionMapperTest {

    private final QuestionMapper questionMapper = Mappers.getMapper(QuestionMapper.class);

    @Test
    void testToDto() {
        // Arrange
        UUID questionId = UUID.randomUUID();
        UUID interviewId = UUID.randomUUID();
        
        Interview interview = new Interview();
        interview.setId(interviewId);
        
        Question question = new Question();
        question.setId(questionId);
        question.setDescription("What is your experience with Java?");
        question.setDurationInMinutes(10);
        question.setInterview(interview);

        // Act
        QuestionDTO dto = questionMapper.toDto(question);

        // Assert
        assertNotNull(dto);
        assertEquals(question.getId(), dto.id());
        assertEquals(question.getDescription(), dto.description());
        assertEquals(question.getDurationInMinutes(), dto.durationInMinutes());
        assertEquals(interviewId, dto.interviewId());
    }

    @Test
    void testToEntity() {
        // Arrange
        UUID questionId = UUID.randomUUID();
        UUID interviewId = UUID.randomUUID();
        
        QuestionDTO dto = new QuestionDTO(
                questionId,
                "Describe your experience with Spring Boot",
                15,
                interviewId
        );

        // Act
        Question entity = questionMapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.description(), entity.getDescription());
        assertEquals(dto.durationInMinutes(), entity.getDurationInMinutes());
        // Interview is ignored in the mapper, so it should be null
        assertNull(entity.getInterview());
    }
    
    @Test
    void testToDtoList() {
        // Arrange
        UUID questionId1 = UUID.randomUUID();
        UUID questionId2 = UUID.randomUUID();
        UUID interviewId = UUID.randomUUID();
        
        Interview interview = new Interview();
        interview.setId(interviewId);
        
        Question question1 = new Question();
        question1.setId(questionId1);
        question1.setDescription("Question 1");
        question1.setDurationInMinutes(5);
        question1.setInterview(interview);
        
        Question question2 = new Question();
        question2.setId(questionId2);
        question2.setDescription("Question 2");
        question2.setDurationInMinutes(10);
        question2.setInterview(interview);
        
        List<Question> questions = new ArrayList<>();
        questions.add(question1);
        questions.add(question2);

        // Act
        List<QuestionDTO> dtos = questionMapper.toDtoList(questions);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(question1.getId(), dtos.get(0).id());
        assertEquals(question1.getDescription(), dtos.get(0).description());
        assertEquals(question1.getDurationInMinutes(), dtos.get(0).durationInMinutes());
        assertEquals(interviewId, dtos.get(0).interviewId());
        
        assertEquals(question2.getId(), dtos.get(1).id());
        assertEquals(question2.getDescription(), dtos.get(1).description());
        assertEquals(question2.getDurationInMinutes(), dtos.get(1).durationInMinutes());
        assertEquals(interviewId, dtos.get(1).interviewId());
    }
}