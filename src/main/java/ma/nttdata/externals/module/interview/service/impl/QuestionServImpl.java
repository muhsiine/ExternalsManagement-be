package ma.nttdata.externals.module.interview.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.commons.constants.InterviewPromptConstants;
import ma.nttdata.externals.commons.exception.InternalServerException;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.dto.InterviewQuestionsPromptPlaceholdersDTO;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.dto.QuestionResponseFromAI;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.entity.Question;
import ma.nttdata.externals.module.interview.mapper.QuestionMapper;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
import ma.nttdata.externals.module.interview.repository.QuestionRepository;
import ma.nttdata.externals.module.interview.service.QuestionServ;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuestionServImpl implements QuestionServ {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final InterviewRepository interviewRepository;
    private final boolean mockFlag;
    private final RestClient aiRestClient;

    public QuestionServImpl(QuestionRepository questionRepository,
                            QuestionMapper questionMapper,
                            InterviewRepository interviewRepository,
                            @Value("${app.mock.flag}") boolean mockFlag,
                            @Qualifier("aiServiceClient") RestClient aiRestClient) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
        this.interviewRepository = interviewRepository;
        this.mockFlag = mockFlag;
        this.aiRestClient = aiRestClient;
    }

    @Override
    public List<QuestionDTO> getAllQuestions() {
        return questionMapper.toDtoList(questionRepository.findAll());
    }

    @Override
    public QuestionDTO getQuestionById(UUID id) {
        Question question = questionRepository.findQuestionById(id);
        return questionMapper.toDto(question);
    }

    @Override
    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        Question question = questionMapper.toEntity(questionDTO);

        // fetch interview first
        UUID interviewId = questionDTO.interviewId();
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));

        question.setInterview(interview);

        Question saved = questionRepository.save(question);
        return questionMapper.toDto(saved);
    }

    @Override
    public QuestionDTO updateQuestion(UUID id, QuestionDTO questionDTO) {
        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        existing.setDescription(questionDTO.description());
        existing.setDurationInMinutes(questionDTO.durationInMinutes());

        Interview interview = interviewRepository.findById(questionDTO.interviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + questionDTO.interviewId()));
        existing.setInterview(interview);

        Question updated = questionRepository.save(existing);
        return questionMapper.toDto(updated);
    }

    @Override
    public void deleteQuestion(UUID id) {
        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        questionRepository.delete(existing);
    }


    @Override
    public List<QuestionDTO> prepareQuestionsFromAIResponse(InterviewQuestionsPromptPlaceholdersDTO generateQuestionsInfo, List<EvaluationTypeDTO> evaluationTypes) {
        try {
            String jsonResponse = mockFlag ?
                    InterviewPromptConstants.JSON_MOCK:
                    generateInterviewQuestionsByPrompt(generateQuestionsInfo, evaluationTypes);

            ObjectMapper objectMapper = new ObjectMapper();

            List<QuestionResponseFromAI> questions = objectMapper.readValue(jsonResponse, new TypeReference<List<QuestionResponseFromAI>>() {});

            return questions.stream()
                    .map(raw -> new QuestionDTO(
                            null,
                            raw.description(),
                            (raw.durationInMinutes() != null && !raw.durationInMinutes().replaceAll("\\D+", "").isEmpty())
                                    ? Integer.parseInt(raw.durationInMinutes().replaceAll("\\D+", ""))
                                    : null,
                            null,
                            null
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new InternalServerException("Failed to parse questions JSON", e);
        }
    }

    @Override
    public String generateInterviewQuestionsByPrompt(InterviewQuestionsPromptPlaceholdersDTO generateQuestionsInfo, List<EvaluationTypeDTO> evaluationTypes){
        String prompt = InterviewPromptConstants.INTERVIEW_QUESTION_GENERATION_PROMPT;

        prompt = prompt.replace(InterviewPromptConstants.CANDIDATE_DATA_PLACEHOLDER, generateQuestionsInfo.candidate().toString())
                .replace(InterviewPromptConstants.OFFER_DATA_PLACEHOLDER, generateQuestionsInfo.offer().toString())
                .replace(InterviewPromptConstants.EVALUATION_TYPE_DATA_PLACEHOLDER,evaluationTypes.toString())
                .replace(InterviewPromptConstants.NUMBER_OF_QUESTIONS_PLACEHOLDER,String.valueOf(generateQuestionsInfo.numberOfQuestions()))
                .replace(InterviewPromptConstants.ESTIMATED_DURATION_PLACEHOLDER,String.valueOf(generateQuestionsInfo.estimatedDuration()))
                .replace(InterviewPromptConstants.JSON_SCHEMA_PLACEHOLDER,InterviewPromptConstants.JSON_SCHEMA);



        return aiRestClient.post()
                .uri("/generateInterviewQuestions")
                .body(prompt)
                .retrieve()
                .body(String.class);

    };

    public List<QuestionDTO> saveAllQuestions(List<QuestionDTO> questionsDTO){
        List<Question> questions = questionsDTO.stream()
                .map(questionMapper::toEntity).collect(Collectors.toList());

        List<Question> savedQuestions = questionRepository.saveAll(questions);

        return questionMapper.toDtoList(savedQuestions);
    }

    public List<Question> findAllQuestionsByInterviewId(UUID interviewId){
        return questionRepository.findByInterviewId(interviewId);
    }

    public List<QuestionDTO> findAllQuestionsDTOSByInterviewId(UUID interviewId){
        List<Question> questions = questionRepository.findByInterviewId(interviewId);
        return questionMapper.toDtoList(questions);
    }
}
