package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.AiEvaluationResponseDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.dto.InterviewEvaluationPlaceholders;
import ma.nttdata.externals.module.interview.dto.QuestionsAndAnswersForEvaluationDTO;

import java.util.List;
import java.util.UUID;

public interface EvaluationServ {
    List<EvaluationDTO> getAllEvaluations();

    EvaluationDTO getEvaluationById(UUID id);

    EvaluationDTO createEvaluation(EvaluationDTO evaluationDTO);

    EvaluationDTO updateEvaluation(UUID id, EvaluationDTO evaluationDTO);

    void deleteEvaluation(UUID id);

    List<AiEvaluationResponseDTO> prepareEvaluationResponseFromAi(QuestionsAndAnswersForEvaluationDTO questionsAndAnswers, InterviewEvaluationPlaceholders placeholders);

    String getInterviewsEvaluationsFromAiByPrompt(QuestionsAndAnswersForEvaluationDTO questionsAndAnswers, InterviewEvaluationPlaceholders placeholders);
}
