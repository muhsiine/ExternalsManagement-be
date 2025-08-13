package ma.nttdata.externals.module.interview.service.impl;

import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.dto.GenerateInterviewQuestionsRequest;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.dto.placeholdersForInterviewQuestionsPromptDTO;
import ma.nttdata.externals.module.interview.service.EvaluationTypeServ;
import ma.nttdata.externals.module.interview.service.InterviewQuestionsUtilServ;
import ma.nttdata.externals.module.interview.service.InterviewServ;
import ma.nttdata.externals.module.interview.service.QuestionServ;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewQuestionsUtilServImpl implements InterviewQuestionsUtilServ {

    private final InterviewServ interviewServ;
    private final EvaluationTypeServ evaluationTypeServ;
    private final QuestionServ questionServ;

    public List<QuestionDTO> generateInterviewQuestions(UUID interviewId,
                                                        GenerateInterviewQuestionsRequest generateInterviewQuestionsRequest){
        placeholdersForInterviewQuestionsPromptDTO placeholders = interviewServ.getPlaceholdersForInterviewQuestionsPrompt(interviewId);
        List<EvaluationTypeDTO> evaluationTypes = evaluationTypeServ.findAllById(generateInterviewQuestionsRequest.evaluationTypesIds());

        List<QuestionDTO> generatedQuestions = questionServ.prepareQuestionsFromAIResponse( placeholders, evaluationTypes);
        List<QuestionDTO> generatedQuestionsWithInterviewId = generatedQuestions.stream()
                .map(q -> new QuestionDTO(
                        null,
                        q.description(),
                        q.durationInMinutes(),
                        interviewId,
                        null
                ))
                .toList();
        List<QuestionDTO> savedQuestions = questionServ.saveAllQuestions(generatedQuestionsWithInterviewId);
        return savedQuestions;
    }
}
