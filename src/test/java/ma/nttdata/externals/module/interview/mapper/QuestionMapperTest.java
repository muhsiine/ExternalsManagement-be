package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.entity.Answer;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.entity.Question;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

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
        UUID answerId = UUID.randomUUID();
        Answer answer = new Answer();
        answer.setId(answerId);

        Interview interview = new Interview();
        interview.setId(interviewId);

        Question question = new Question();
        question.setId(questionId);
        question.setDescription("What is your experience with Java?");
        question.setDurationInMinutes(10);
        question.setInterview(interview);
        question.setAnswer(answer);

        // Act
        QuestionDTO dto = questionMapper.toDto(question);

        // Assert
        assertNotNull(dto);
        assertEquals(question.getId(), dto.id());
        assertEquals(question.getDescription(), dto.description());
        assertEquals(question.getDurationInMinutes(), dto.durationInMinutes());
        assertEquals(interviewId, dto.interviewId());
        assertEquals(answerId, dto.answerId());
    }

    @Test
    void testToEntity() {
        // Arrange
        UUID questionId = UUID.randomUUID();
        UUID interviewId = UUID.randomUUID();
        UUID answerId = UUID.randomUUID();

        QuestionDTO dto = new QuestionDTO(
                questionId,
                "Describe your experience with Spring Boot",
                15,
                interviewId,
                answerId
        );

        // Act
        Question entity = questionMapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.description(), entity.getDescription());
        assertEquals(dto.durationInMinutes(), entity.getDurationInMinutes());
        // Interview and Answer are ignored in the mapper, so they should be null
        assertNotNull(entity.getInterview());
        assertNull(entity.getAnswer());
    }

    @Test
    void testToDtoList() {
        // Arrange
        UUID questionId1 = UUID.randomUUID();
        UUID questionId2 = UUID.randomUUID();
        UUID interviewId = UUID.randomUUID();
        UUID answerId1 = UUID.randomUUID();
        UUID answerId2 = UUID.randomUUID();

        Interview interview = new Interview();
        interview.setId(interviewId);

        Answer answer1 = new Answer();
        answer1.setId(answerId1);
        Answer answer2 = new Answer();
        answer2.setId(answerId2);

        Question question1 = new Question();
        question1.setId(questionId1);
        question1.setDescription("Question 1");
        question1.setDurationInMinutes(5);
        question1.setInterview(interview);
        question1.setAnswer(answer1);

        Question question2 = new Question();
        question2.setId(questionId2);
        question2.setDescription("Question 2");
        question2.setDurationInMinutes(10);
        question2.setInterview(interview);
        question2.setAnswer(answer2);

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
        assertEquals(answerId1, dtos.get(0).answerId());

        assertEquals(question2.getId(), dtos.get(1).id());
        assertEquals(question2.getDescription(), dtos.get(1).description());
        assertEquals(question2.getDurationInMinutes(), dtos.get(1).durationInMinutes());
        assertEquals(interviewId, dtos.get(1).interviewId());
        assertEquals(answerId2, dtos.get(1).answerId());
    }
}
