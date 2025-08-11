package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.InterviewEvaluationsRequestDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface InterviewEvaluationUtilServ {
    List<Evaluation> prepareInterviewEvaluation(UUID interviewId, InterviewEvaluationsRequestDTO interviewEvaluationsRequest);
}
