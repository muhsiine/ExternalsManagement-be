package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.AnswerDTO;

import java.util.List;
import java.util.UUID;

public interface AnswerServ {

    List<AnswerDTO> getAllAnswers();

    AnswerDTO getAnswerById(UUID id);

    AnswerDTO createAnswer(AnswerDTO answerDTO);

    AnswerDTO updateAnswer(UUID id, AnswerDTO answerDTO);

    void deleteAnswer(UUID id);
}
