package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.dto.placeholdersForInterviewQuestionsPromptDTO;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.entity.Question;
import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface QuestionServ {

    List<QuestionDTO> getAllQuestions();

    QuestionDTO getQuestionById(UUID id);

    QuestionDTO createQuestion(QuestionDTO questionDTO);

    QuestionDTO updateQuestion(UUID id, QuestionDTO questionDTO);

    void deleteQuestion(UUID id);

    List<QuestionDTO> prepareQuestionsFromAIResponse(placeholdersForInterviewQuestionsPromptDTO  placeholders, List<EvaluationTypeDTO> evaluationTypes, PromptDTO prompt);

    String generateInterviewQuestionsByPrompt(placeholdersForInterviewQuestionsPromptDTO  placeholders, List<EvaluationTypeDTO> evaluationTypes, PromptDTO prompt);

    List<QuestionDTO> saveAllQuestions(List<QuestionDTO> questions);

    List<Question> findAllQuestionsByInterviewId(UUID interviewId);

    List<QuestionDTO> findAllQuestionsDTOSByInterviewId(UUID interviewId);

    ResponseEntity<byte[]> generateInterviewQuestionAudio(String text);
}
