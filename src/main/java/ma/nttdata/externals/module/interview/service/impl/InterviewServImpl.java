package ma.nttdata.externals.module.interview.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.entity.Contact;
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

import ma.nttdata.externals.module.offer.entity.Offer;
import org.springframework.beans.factory.annotation.Value;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.mapper.OfferMapper;
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


    private final String interviewBaseLink;

    private final OfferMapper offerMapper;

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
            CandidateMapper candidateMapper,
            @Value("${interview.baseLink}") String interviewBaseLink,
            OfferMapper offerMapper
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
        this.interviewBaseLink = interviewBaseLink;
        this.offerMapper = offerMapper;
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

    @Override
    public String saveInterviewLink(String token, UUID interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found",interviewId));


        String interviewLink = interviewBaseLink+token;
        interview.setLink(interviewLink);
        interviewRepository.save(interview);
        return interviewLink;
    }
    @Override
    public GenerateQuestionsInfoDTO getInterviewCandidateAndOfferAndEvaluationTypes(UUID interviewId){
        Interview interview = interviewRepository.findWithCandidateAndOfferAndEvaluationTypesById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with ID: " + interviewId));

        CandidateDTO candidate = candidateMapper.candidateToCandidateDTO(interview.getCandidate());

        OfferDTO offer = offerMapper.toDto(interview.getOffer());

        List<EvaluationTypeDTO> evaluationTypes = interview.getEvaluations()
                .stream().map(Evaluation::getEvaluationType)
                .distinct()
                .map(evaluationTypeMapper::toDto)
                .toList();

        GenerateQuestionsInfoDTO generateQuestionsInfo = new GenerateQuestionsInfoDTO(
                candidate,
                offer,
                evaluationTypes
        );

        return generateQuestionsInfo;
    }

    @Override
    public SendEmailDTO getEmailInfo(UUID interviewId){
        Interview interview = interviewRepository.findWithCandidateAndOfferById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found",interviewId));;

        Candidate candidate = interview.getCandidate();
        Offer offer = interview.getOffer();

        String email = candidate.getContacts().stream()
                .filter(c -> {
                    String type = c.getContactType();
                    return type != null && (type.equalsIgnoreCase("email") || type.equalsIgnoreCase("mail"));
                })
                .map(Contact::getContactValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Candidate email not found"));

        return new SendEmailDTO(
                candidate.getFullName(),
                offer.getTitle(),
                email,
                interview.getScheduledAt(),
                interview.getLink()
        );
    }
}


