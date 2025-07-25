package ma.nttdata.externals.module.interview.service;


import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.dto.*;
import org.springframework.data.crossstore.ChangeSetPersister;


import java.util.List;
import java.util.UUID;


public interface InterviewServ {

    // new interview
    InterviewDTO createInterview(InterviewDTO interviewDTO);

    // list all interviews
    List<InterviewDTO> getAllInterviews();

    InterviewDTO getInterviewById(UUID id);

    InterviewDTO updateInterview(UUID id , InterviewDTO interviewDTO);

    void deleteInterview(UUID id );

    // all interviews of an offer
    List<InterviewDTO> getInterviewsByOfferId(UUID offerId);

    // all questions of an interview
    List<QuestionDTO> getQuestionsByInterviewId(UUID interviewId);

    // corresponding answer  for question
    AnswerDTO getAnswerOfQuestion(UUID questionId);

    // get the answer of question id
    List<AnswerDTO> getAnswersByQuestionId(UUID questionId);

    // get candidate passed interview id
    CandidateDTO getCandidateByInterviewId(UUID interviewId) throws ChangeSetPersister.NotFoundException;

    // get evaluations of an interview
    List<EvaluationDTO> getEvaluationsOfInterview(UUID interviewId);


    EvaluationTypeDTO getEvaluationTypeOfEvaluation(UUID evaluationId);

    String saveInterviewLink(String token, UUID interviewId);
}
