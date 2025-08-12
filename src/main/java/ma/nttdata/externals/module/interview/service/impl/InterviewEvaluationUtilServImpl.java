package ma.nttdata.externals.module.interview.service.impl;

import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.interview.dto.EvaluationsAIResponseDTO;
import ma.nttdata.externals.module.interview.dto.InterviewEvaluationDTO;
import ma.nttdata.externals.module.interview.dto.InterviewEvaluationsRequestDTO;
import ma.nttdata.externals.module.interview.dto.PlaceholdersForInterviewEvaluationPromptDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.mapper.EvaluationMapper;
import ma.nttdata.externals.module.interview.service.EvaluationServ;
import ma.nttdata.externals.module.interview.service.InterviewEvaluationUtilServ;
import ma.nttdata.externals.module.interview.service.InterviewServ;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewEvaluationUtilServImpl implements InterviewEvaluationUtilServ {

    private final EvaluationServ evaluationServ;
    private final InterviewServ interviewServ;
    private final EvaluationMapper evaluationMapper;

    @Override
    public List<Evaluation> prepareInterviewEvaluation(UUID interviewId, InterviewEvaluationsRequestDTO interviewEvaluationsRequest) {
        PlaceholdersForInterviewEvaluationPromptDTO placeholders = interviewServ.getInterviewEvaluationPlaceholders(interviewId);
        List<EvaluationsAIResponseDTO> aiEvaluationResponse = evaluationServ.prepareEvaluationsDTOFromAiResponse(interviewEvaluationsRequest,placeholders);
        List<Evaluation> evaluations = evaluationServ.getAllEvaluationsByInterviewID(interviewId);
        List<Evaluation> savedEvaluations = evaluationServ.saveAIEvaluationResponse(aiEvaluationResponse,evaluations);
        return savedEvaluations;
    }

    @Override
    public InterviewEvaluationDTO getInterviewEvaluations(UUID interviewId){
        List<Evaluation> evaluations = evaluationServ.getAllEvaluationsByInterviewID(interviewId);
        return evaluationMapper.mapEvaluationToInterviewEvaluation(evaluations);
    }
}
