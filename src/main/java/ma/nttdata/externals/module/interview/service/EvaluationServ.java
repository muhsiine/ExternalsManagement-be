package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationsAIResponseDTO;
import ma.nttdata.externals.module.interview.dto.InterviewEvaluationsRequestDTO;
import ma.nttdata.externals.module.interview.dto.PlaceholdersForInterviewEvaluationPromptDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;

import java.util.List;
import java.util.UUID;

public interface EvaluationServ {
    List<EvaluationDTO> getAllEvaluations();

    EvaluationDTO getEvaluationById(UUID id);

    EvaluationDTO createEvaluation(EvaluationDTO evaluationDTO);

    EvaluationDTO updateEvaluation(UUID id, EvaluationDTO evaluationDTO);

    void deleteEvaluation(UUID id);

    List<EvaluationsAIResponseDTO> prepareEvaluationsDTOFromAiResponse(InterviewEvaluationsRequestDTO interviewEvaluationsRequest, PlaceholdersForInterviewEvaluationPromptDTO placeholders);

    String getInterviewsEvaluationsFromAiByPrompt(InterviewEvaluationsRequestDTO interviewEvaluationsRequest, PlaceholdersForInterviewEvaluationPromptDTO placeholders);

    List<Evaluation> saveAIEvaluationResponse(List<EvaluationsAIResponseDTO> aiEvaluationResponse, List<Evaluation> evaluations);

    List<Evaluation> getAllEvaluationsByInterviewID(UUID interviewId);
}
