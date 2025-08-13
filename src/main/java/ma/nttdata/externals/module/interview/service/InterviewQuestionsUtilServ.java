package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.GenerateInterviewQuestionsRequest;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;

import java.util.List;
import java.util.UUID;

public interface InterviewQuestionsUtilServ {

    List<QuestionDTO> generateInterviewQuestions(UUID interviewId,
                                                 GenerateInterviewQuestionsRequest generateInterviewQuestionsRequest);
}
