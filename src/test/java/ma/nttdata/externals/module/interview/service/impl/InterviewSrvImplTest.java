package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.*;
import ma.nttdata.externals.module.interview.mapper.*;
import ma.nttdata.externals.module.interview.repository.*;
import ma.nttdata.externals.module.offer.entity.Offer;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InterviewSrvImplTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private EvaluationTypeRepository evaluationTypeRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private InterviewMapper interviewMapper;

    @Mock
    private QuestionMapper questionMapper;

    @Mock
    private AnswerMapper answerMapper;

    @Mock
    private EvaluationMapper evaluationMapper;

    @Mock
    private EvaluationTypeMapper evaluationTypeMapper;

    @Mock
    private CandidateMapper candidateMapper;

    @InjectMocks
    private InterviewServImpl interviewServ;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateInterview() {
        UUID offerId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();

        InterviewDTO dto = new InterviewDTO(null, LocalDateTime.now(), LocalDateTime.now().plusHours(1), "desc", "link", "feedback", offerId, candidateId);
        Interview interview = new Interview();
        interview.setId(UUID.randomUUID());

        QuestionDTO questiondto = new QuestionDTO( null , "" ,  0 ,null);
        Question question = new Question();

        Offer offer = new Offer();
        offer.setId(offerId);
        Candidate candidate = new Candidate();
        candidate.setId(candidateId);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(interviewMapper.toEntity(dto)).thenReturn(interview);
        when(interviewRepository.save(interview)).thenReturn(interview);
        when(interviewMapper.toDto(interview)).thenReturn(dto);

        InterviewDTO result = interviewServ.createInterview(dto);

        assertEquals(dto, result);
        verify(interviewRepository).save(interview);
    }

    @Test
    void testGetAllInterviews() {
        List<Interview> interviews = List.of(new Interview());
        when(interviewRepository.findAll()).thenReturn(interviews);
        when(interviewMapper.toDtoList(interviews)).thenReturn(List.of(new InterviewDTO(null, null, null, null, null, null, null, null)));

        List<InterviewDTO> result = interviewServ.getAllInterviews();

        assertEquals(1, result.size());
    }

    @Test
    void testGetInterviewById() {
        UUID id = UUID.randomUUID();
        Interview interview = new Interview();
        when(interviewRepository.findById(id)).thenReturn(Optional.of(interview));
        when(interviewMapper.toDto(interview)).thenReturn(new InterviewDTO(id, null, null, null, null, null, null, null));

        InterviewDTO result = interviewServ.getInterviewById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    void testUpdateInterview() {
        UUID id = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();

        Interview interview = new Interview();
        interview.setId(id);

        InterviewDTO dto = new InterviewDTO(id, LocalDateTime.now(), LocalDateTime.now().plusHours(1), "desc", "link", "feedback", offerId, candidateId);

        Offer offer = new Offer();
        offer.setId(offerId);
        Candidate candidate = new Candidate();
        candidate.setId(candidateId);

        when(interviewRepository.findById(id)).thenReturn(Optional.of(interview));
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(interviewMapper.toEntity(dto)).thenReturn(interview);
        when(interviewRepository.save(interview)).thenReturn(interview);
        when(interviewMapper.toDto(interview)).thenReturn(dto);

        InterviewDTO result = interviewServ.updateInterview(id, dto);

        assertEquals(dto, result);
        verify(interviewRepository).save(interview);
    }

    @Test
    void testDeleteInterview() {
        UUID id = UUID.randomUUID();
        Interview interview = new Interview();
        when(interviewRepository.findById(id)).thenReturn(Optional.of(interview));

        interviewServ.deleteInterview(id);

        verify(interviewRepository).delete(interview);
    }

    @Test
    void testGetInterviewsByOfferId() {
        UUID offerId = UUID.randomUUID();
        List<Interview> list = List.of(new Interview());
        when(interviewRepository.findByOfferId(offerId)).thenReturn(list);
        when(interviewMapper.toDtoList(list)).thenReturn(List.of(new InterviewDTO(null, null, null, null, null, null, offerId, null)));

        List<InterviewDTO> result = interviewServ.getInterviewsByOfferId(offerId);

        assertEquals(1, result.size());
    }

    @Test
    void testGetQuestionsByInterviewId() {
        UUID interviewId = UUID.randomUUID();
        List<Question> list = List.of(new Question());
        when(questionRepository.findByInterviewId(interviewId)).thenReturn(list);
        when(questionMapper.toDtoList(list)).thenReturn(List.of(new QuestionDTO(null ," " ,0 , null)));

        List<QuestionDTO> result = interviewServ.getQuestionsByInterviewId(interviewId);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAnswerOfQuestion() {
        UUID questionId = UUID.randomUUID();
        Answer answer = new Answer();
        when(answerRepository.findByQuestionId(questionId));
        when(answerMapper.toDto(answer)).thenReturn(new AnswerDTO(null , "" , 0 , null));

        AnswerDTO result = interviewServ.getAnswerOfQuestion(questionId);

        assertNotNull(result);
    }

    @Test
    void testGetAnswersByQuestionId() {
        UUID questionId = UUID.randomUUID();
        List<Answer> list = List.of(new Answer());
        when(answerRepository.findByQuestionId(questionId)).thenReturn(list);
        when(answerMapper.toDtoList(list)).thenReturn(List.of(new AnswerDTO(null , "", 0 , null)));

        List<AnswerDTO> result = interviewServ.getAnswersByQuestionId(questionId);

        assertEquals(1, result.size());
    }

    @Test
    void testGetCandidateByInterviewId() throws Exception {
        UUID interviewId = UUID.randomUUID();
        Interview interview = new Interview();
        Candidate candidate = new Candidate();
        candidate.setId(UUID.randomUUID());
        interview.setCandidate(candidate);

        when(interviewRepository.findById(interviewId)).thenReturn(Optional.of(interview));
        when(candidateMapper.candidateToCandidateDTO(candidate)).thenReturn(new CandidateDTO(
                candidate.getId(), null, null, 0, null, null, null, null, null, null, null, null, null, null));

        CandidateDTO result = interviewServ.getCandidateByInterviewId(interviewId);

        assertNotNull(result);
    }

    @Test
    void testGetEvaluationsOfInterview() {
        UUID interviewId = UUID.randomUUID();
        List<Evaluation> evaluations = List.of(new Evaluation());
        when(evaluationRepository.findByInterviewId(interviewId)).thenReturn(evaluations);
        when(evaluationMapper.toDtoList(evaluations)).thenReturn(List.of(new EvaluationDTO(null , 0.0 ,"" , null , null)));

        List<EvaluationDTO> result = interviewServ.getEvaluationsOfInterview(interviewId);

        assertEquals(1, result.size());
    }

    @Test
    void testGetEvaluationTypeOfEvaluation() {
        UUID evaluationId = UUID.randomUUID();

        Evaluation evaluation = new Evaluation();
        EvaluationType evaluationType = new EvaluationType();
        evaluation.setEvaluationType(evaluationType);

        when(evaluationRepository.findTypeById(evaluationId)).thenReturn(Optional.of(evaluation));
        when(evaluationTypeRepository.findById(any())).thenReturn(Optional.of(evaluationType));
        when(evaluationTypeMapper.toDto(evaluationType)).thenReturn(new EvaluationTypeDTO(null , "" , 0.0));

        EvaluationTypeDTO result = interviewServ.getEvaluationTypeOfEvaluation(evaluationId);

        assertNotNull(result);
    }
}
