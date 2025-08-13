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
import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import ma.nttdata.externals.module.prompt.service.PromptService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewQuestionsUtilServImpl implements InterviewQuestionsUtilServ {

    private final InterviewServ interviewServ;
    private final EvaluationTypeServ evaluationTypeServ;
    private final QuestionServ questionServ;
    private final PromptService promptService;

    public List<QuestionDTO> generateInterviewQuestions(UUID interviewId,
                                                        GenerateInterviewQuestionsRequest generateInterviewQuestionsRequest){
        placeholdersForInterviewQuestionsPromptDTO placeholders = interviewServ.getPlaceholdersForInterviewQuestionsPrompt(interviewId);
        List<EvaluationTypeDTO> evaluationTypes = evaluationTypeServ.findAllById(generateInterviewQuestionsRequest.evaluationTypesIds());
        PromptDTO prompt = promptService.findByPromptCode(generateInterviewQuestionsRequest.promptCode());
        List<QuestionDTO> generatedQuestions = questionServ.prepareQuestionsFromAIResponse( placeholders, evaluationTypes,prompt);
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
