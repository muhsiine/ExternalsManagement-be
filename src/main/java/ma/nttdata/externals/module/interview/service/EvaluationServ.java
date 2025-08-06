package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.*;
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

    List<EvaluationWithInterviewAndEvaluationTypeDTO> getAllEvaluationsWithInterviewByInterviewId(UUID interviewId);

}
