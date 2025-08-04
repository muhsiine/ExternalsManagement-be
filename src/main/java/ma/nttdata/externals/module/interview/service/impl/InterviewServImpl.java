package ma.nttdata.externals.module.interview.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.*;
import ma.nttdata.externals.module.interview.mapper.*;
import ma.nttdata.externals.module.interview.mapper.EvaluationTypeMapper;
import ma.nttdata.externals.module.interview.repository.AnswerRepository;
import ma.nttdata.externals.module.interview.repository.EvaluationRepository;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
import ma.nttdata.externals.module.interview.repository.QuestionRepository;
import ma.nttdata.externals.module.interview.service.InterviewServ;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class InterviewServImpl implements InterviewServ {

    private final InterviewMapper interviewMapper;
    private final InterviewRepository interviewRepository;

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    private final AnswerMapper answerMapper;
    private final AnswerRepository answerRepository;

    private final EvaluationRepository evaluationRepository ;
    private final EvaluationMapper evaluationMapper ;

    private final EvaluationTypeMapper evaluationTypeMapper;

    private final CandidateMapper candidateMapper ;

    public InterviewServImpl(
            InterviewMapper interviewMapper,
            InterviewRepository interviewRepository ,
            QuestionRepository questionRepository ,
            QuestionMapper questionMapper,
            AnswerRepository answerRepository  ,
            AnswerMapper answerMapper ,
            EvaluationRepository evaluationRepository ,
            EvaluationMapper evaluationMapper ,
            EvaluationTypeMapper evaluationTypeMapper ,
            CandidateMapper candidateMapper
    ) {
        this.interviewMapper = interviewMapper;
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository ;
        this.questionMapper = questionMapper;
        this.answerRepository = answerRepository ;
        this.answerMapper = answerMapper ;
        this.evaluationMapper = evaluationMapper ;
        this.evaluationRepository = evaluationRepository;
        this.evaluationTypeMapper= evaluationTypeMapper;
        this.candidateMapper = candidateMapper;
    }

    // new interview
    @Override
    public InterviewDTO createInterview(InterviewDTO interviewDTO) {
        Interview interview = interviewMapper.toEntity(interviewDTO);
        Interview savedInterview = interviewRepository.save(interview);
        return interviewMapper.toDto(savedInterview);
    }

    @Override
    public List<InterviewDTO> getAllInterviews() {
        List<Interview> interviews = interviewRepository.findAll();
        return interviewMapper.toDtoList(interviews);
    }

    // get interview by offer id
    @Override
    public List<InterviewDTO> getInterviewsByOfferId(UUID offerId) {
        List<Interview> interviews = interviewRepository.findByOfferId(offerId);
        return interviewMapper.toDtoList(interviews);
    }

    // get interview by id
    @Override
    public InterviewDTO getInterviewById(UUID id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found with Id " + id));
        return interviewMapper.toDto(interview);
    }

    @Override
    public InterviewDTO updateInterview(UUID id, InterviewDTO interviewDTO) {
        Interview existingInterview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found with Id " + id));

        interviewMapper.updateInterviewFromDto(interviewDTO, existingInterview);

        Interview updatedInterview = interviewRepository.save(existingInterview);
        return interviewMapper.toDto(updatedInterview);
    }


    // delete
    @Override
    public void deleteInterview(UUID id) {
        if (!interviewRepository.existsById(id)) {
            throw new RuntimeException("Interview not found with ID " + id);
        }
        interviewRepository.deleteById(id);
    }

    // get questions of an interview
    @Override
    public List<QuestionDTO> getQuestionsByInterviewId(UUID interviewId) {
        List<Question> questions = questionRepository.findByInterviewId(interviewId);
        return questionMapper.toDtoList(questions);
    }

    @Override
    public AnswerDTO getAnswerOfQuestion(UUID questionId) {
        return null;
    }



    // get the answer of question id
    @Override
    public  AnswerDTO getAnswerByQuestionId(UUID questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));

        Answer answer = question.getAnswer();

        return answerMapper.toDto(answer);
    }
    @Override
    public CandidateDTO getCandidateByInterviewId(UUID interviewId) throws ChangeSetPersister.NotFoundException {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        Candidate candidate = interview.getCandidate();
        if (candidate == null) {
            throw new ChangeSetPersister.NotFoundException();
        }

        return candidateMapper.candidateToCandidateDTO(
                candidate);
    }

    @Override
    public List<EvaluationDTO> getEvaluationsOfInterview(UUID interviewId) {
        List<Evaluation> evaluations = evaluationRepository.findByInterviewId(interviewId);
        return evaluationMapper.toDtoList(evaluations);
    }

    @Override
    public EvaluationTypeDTO getEvaluationTypeOfEvaluation(UUID id) {
        Optional<Evaluation> evaluationOptional = evaluationRepository.findTypeById(id);

        if (evaluationOptional.isEmpty()) {
            throw new EntityNotFoundException("Evaluation not found with id: " + id);
        }

        Evaluation evaluation = evaluationOptional.get();

        EvaluationType evaluationType = evaluation.getEvaluationType(); // assuming this is the entity

        return evaluationTypeMapper.toDto(evaluationType);
    }
    //add comment
    @Override
    public InterviewDTO addComment(UUID id, String comment) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found with Id " + id));

        // Assuming Interview entity has a `comment` field
        interview.setComment(comment);

        Interview updatedInterview = interviewRepository.save(interview);
        return interviewMapper.toDto(updatedInterview);
    }




}


