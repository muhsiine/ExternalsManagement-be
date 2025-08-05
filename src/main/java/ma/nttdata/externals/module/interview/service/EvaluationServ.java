package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.AiEvaluationResponseDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.dto.InterviewEvaluationPlaceholdersDTO;
import ma.nttdata.externals.module.interview.dto.QuestionsAndAnswersForEvaluationDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;

import java.util.List;
import java.util.UUID;

public interface EvaluationServ {
    List<EvaluationDTO> getAllEvaluations();

    EvaluationDTO getEvaluationById(UUID id);

    EvaluationDTO createEvaluation(EvaluationDTO evaluationDTO);

    EvaluationDTO updateEvaluation(UUID id, EvaluationDTO evaluationDTO);

    void deleteEvaluation(UUID id);

    List<AiEvaluationResponseDTO> prepareEvaluationResponseFromAi(List<QuestionsAndAnswersForEvaluationDTO> questionsAndAnswers, InterviewEvaluationPlaceholdersDTO placeholders);

    String getInterviewsEvaluationsFromAiByPrompt(List<QuestionsAndAnswersForEvaluationDTO> questionsAndAnswers, InterviewEvaluationPlaceholdersDTO placeholders);

    List<Evaluation> saveAIEvaluationResponse(List<AiEvaluationResponseDTO> aiEvaluationResponse, InterviewEvaluationPlaceholdersDTO placeholders);

    List<Evaluation> getAllEvaluationsByInterviewID(UUID interviewId);

    List<EvaluationDTO> getAllEvaluationsDTOByInterviewID(UUID interviewId);
}
