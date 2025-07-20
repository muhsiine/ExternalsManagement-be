package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.QuestionDTO;

import java.util.List;
import java.util.UUID;

public interface QuestionServ {

    List<QuestionDTO> getAllQuestions();

    QuestionDTO getQuestionById(UUID id);

    QuestionDTO createQuestion(QuestionDTO questionDTO);

    QuestionDTO updateQuestion(UUID id, QuestionDTO questionDTO);

    void deleteQuestion(UUID id);
}
