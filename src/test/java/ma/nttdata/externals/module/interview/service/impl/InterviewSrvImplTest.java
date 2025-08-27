package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.commons.constants.InterviewEvaluationPromptConstants;
import ma.nttdata.externals.commons.constants.InterviewPromptConstants;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.dto.ContactDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.entity.Contact;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.*;
import ma.nttdata.externals.module.interview.mapper.*;
import ma.nttdata.externals.module.interview.repository.*;
import ma.nttdata.externals.module.interview.service.EvaluationServ;
import ma.nttdata.externals.module.interview.service.EvaluationTypeServ;
import ma.nttdata.externals.module.interview.service.QuestionServ;
import ma.nttdata.externals.module.interview.service.TextToSpeechServ;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.entity.Offer;
import ma.nttdata.externals.module.offer.mapper.OfferMapper;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import ma.nttdata.externals.module.prompt.service.PromptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InterviewSrvImplTest {

    @Mock private InterviewRepository interviewRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private EvaluationRepository evaluationRepository;
    @Mock private EvaluationTypeRepository evaluationTypeRepository;
    @Mock private CandidateRepository candidateRepository;
    @Mock private OfferRepository offerRepository;

    @Mock private InterviewMapper interviewMapper;
    @Mock private QuestionMapper questionMapper;
    @Mock private AnswerMapper answerMapper;
    @Mock private EvaluationMapper evaluationMapper;
    @Mock private EvaluationTypeMapper evaluationTypeMapper;
    @Mock private CandidateMapper candidateMapper;
    @Mock private OfferMapper offerMapper;

    private InterviewServImpl interviewServ;

    private static final String TEST_BASE_LINK = "http://localhost:8080/interview/";
    @Mock private EvaluationServ evaluationServ;
    @Mock private  PromptService promptServ;
    @Mock private  EvaluationTypeServ evaluationTypeServ;
    @Mock private QuestionServ questionServ;
    @Mock private TextToSpeechServ textToSpeechServ;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        interviewServ = new InterviewServImpl(
                interviewMapper,
                interviewRepository,
                questionRepository,
                questionMapper,
                answerRepository,
                answerMapper,
                evaluationRepository,
                evaluationMapper,
                evaluationTypeMapper,
                candidateMapper,
                TEST_BASE_LINK,
                offerMapper,
                evaluationServ,
                promptServ,
                evaluationTypeServ,
                questionServ,
                textToSpeechServ
        );
    }

    @Test
    void testCreateInterview() {
        UUID offerId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();

        InterviewDTO dto = new InterviewDTO(
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "desc",
                "link",
                "feedback",
                LocalDateTime.now().plusDays(2),
                "Good communication during the meeting",
                3,
                30,
                offerId,
                candidateId,
                new ArrayList<>(),
                new ArrayList<>()
        );

        Interview interview = new Interview();
        interview.setId(UUID.randomUUID());

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
        when(interviewMapper.toDtoList(interviews)).thenReturn(List.of(
                new InterviewDTO(null, null, null, null, null, null, null, null, 7,40,null, null, new ArrayList<>(), new ArrayList<>())
        ));

        List<InterviewDTO> result = interviewServ.getAllInterviews();

        assertEquals(1, result.size());
    }

    @Test
    void testGetInterviewById() {
        UUID id = UUID.randomUUID();
        Interview interview = new Interview();
        when(interviewRepository.findById(id)).thenReturn(Optional.of(interview));
        when(interviewMapper.toDto(interview)).thenReturn(
                new InterviewDTO(id, null, null, null, null, null, null, null,5,30, null, null, new ArrayList<>(), new ArrayList<>())
        );

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

        InterviewDTO dto = new InterviewDTO(
                id,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "desc",
                "link",
                "feedback",
                LocalDateTime.now().plusDays(2),
                "Interview in general passed smoothly",
                3,
                30,
                offerId,
                candidateId,
                new ArrayList<>(),
                new ArrayList<>()
        );

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
    void testGetInterviewsByOfferId() {
        UUID offerId = UUID.randomUUID();
        List<Interview> list = List.of(new Interview());
        when(interviewRepository.findByOfferId(offerId)).thenReturn(list);
        when(interviewMapper.toDtoList(list)).thenReturn(List.of(
                new InterviewDTO(null, null, null, null, null, null, null, null,8,50, offerId, null, new ArrayList<>(), new ArrayList<>())
        ));

        List<InterviewDTO> result = interviewServ.getInterviewsByOfferId(offerId);

        assertEquals(1, result.size());
    }

    @Test
    void testGetQuestionsByInterviewId() {
        UUID interviewId = UUID.randomUUID();
        List<Question> list = List.of(new Question());
        when(questionRepository.findByInterviewId(interviewId)).thenReturn(list);
        when(questionMapper.toDtoList(list)).thenReturn(List.of(
                new QuestionDTO(null, "", 0, null, null)
        ));

        List<QuestionDTO> result = interviewServ.getQuestionsByInterviewId(interviewId);

        assertEquals(1, result.size());
    }



    @Test
    void testGetCandidateByInterviewId() {
        UUID interviewId = UUID.randomUUID();
        Interview interview = new Interview();
        Candidate candidate = new Candidate();
        candidate.setId(UUID.randomUUID());
        interview.setCandidate(candidate);

        when(interviewRepository.findById(interviewId)).thenReturn(Optional.of(interview));
        when(candidateMapper.candidateToCandidateDTO(candidate)).thenReturn(
                new CandidateDTO(candidate.getId(), null, null, 0, null, null, null, null, null, null, null, null, null, null , null)
        );

        CandidateDTO result = null;
        try {
            result = interviewServ.getCandidateByInterviewId(interviewId);
        } catch (ChangeSetPersister.NotFoundException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(result);
    }

    @Test
    void testGetEvaluationsOfInterview() {
        UUID interviewId = UUID.randomUUID();
        List<Evaluation> evaluations = List.of(new Evaluation());
        when(evaluationRepository.findByInterviewId(interviewId)).thenReturn(evaluations);
        when(evaluationMapper.toDtoList(evaluations)).thenReturn(List.of(
                new EvaluationDTO(null, 0.0, "", null, null)
        ));

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
        when(evaluationTypeMapper.toDto(evaluationType)).thenReturn(new EvaluationTypeDTO(null, "", 0.0));

        EvaluationTypeDTO result = interviewServ.getEvaluationTypeOfEvaluation(evaluationId);

        assertNotNull(result);
    }

    @Test
    void getInterviewCandidateWithoutContactsAndOffer_should_return_interview_candidate_and_offer() {

        UUID interviewId = UUID.randomUUID();

        Candidate candidate = new Candidate();
        candidate.setId(UUID.randomUUID());

        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());

        EvaluationType evalType1 = new EvaluationType();
        evalType1.setId(UUID.randomUUID());

        Evaluation evaluation1 = new Evaluation();
        evaluation1.setEvaluationType(evalType1);

        List<Evaluation> evaluations = List.of(evaluation1);

        Interview interview = new Interview();
        interview.setId(interviewId);
        interview.setCandidate(candidate);
        interview.setOffer(offer);
        interview.setEvaluations(evaluations);

        CandidateDTO candidateDTO = new CandidateDTO(candidate.getId(), null, null, 0, null, null, null, null, null, null, null, null, null, null, null);
        OfferDTO offerDTO = new OfferDTO(offer.getId(), null, null, null, null);

        when(interviewRepository.findWithCandidateWithoutContactsAndOfferById(interviewId)).thenReturn(Optional.of(interview));
        when(candidateMapper.candidateToCandidateDTO(candidate)).thenReturn(candidateDTO);
        when(offerMapper.toDto(offer)).thenReturn(offerDTO);

        placeholdersForInterviewQuestionsPromptDTO result = interviewServ.getPlaceholdersForInterviewQuestionsPrompt(interviewId);

        assertNotNull(result);
        assertEquals(candidateDTO, result.candidate());
        assertEquals(offerDTO, result.offer());


        verify(interviewRepository).findWithCandidateWithoutContactsAndOfferById(interviewId);
        verify(candidateMapper).candidateToCandidateDTO(candidate);
        verify(offerMapper).toDto(offer);

    }

    @Test
    void should_fetch_Interview_Evaluation_placeholders(){
        UUID interviewId = UUID.randomUUID();

        Candidate candidate = new Candidate();
        candidate.setId(UUID.randomUUID());
        candidate.setFullName("habib");

        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());
        offer.setTitle("Backend Engineer");

        EvaluationType evalType1 = new EvaluationType();
        evalType1.setId(UUID.randomUUID());
        String description = "communication Skills";
        evalType1.setDescription(description);

        Evaluation evaluation1 = new Evaluation();
        evaluation1.setEvaluationType(evalType1);

        List<Evaluation> evaluations = List.of(evaluation1);

        Interview interview = new Interview();
        interview.setId(interviewId);
        interview.setCandidate(candidate);
        interview.setOffer(offer);
        interview.setEvaluations(evaluations);
        interview.setEstimatedDuration(30);
        interview.setNumberOfQuestions(15);
        CandidateDTO candidateDTO = new CandidateDTO(candidate.getId(), "habib", null, 0, null, null, null, null, null, null, null, null, null, null, null);
        OfferDTO offerDTO = new OfferDTO(offer.getId(), "Backend Engineer", null, null,null);

        when(interviewRepository.findById(interviewId)).thenReturn(Optional.of(interview));
        when(candidateMapper.candidateToCandidateDTO(candidate)).thenReturn(candidateDTO);
        when(offerMapper.toDto(offer)).thenReturn(offerDTO);

        PlaceholdersForInterviewEvaluationPromptDTO  placeholders = interviewServ.getInterviewEvaluationPlaceholders(interviewId);

        assertNotNull(placeholders );
        assertEquals(candidateDTO, placeholders.candidate());
        assertEquals(offerDTO, placeholders.offer());
        assertEquals(List.of(evaluation1.getEvaluationType()), placeholders.evaluationType());

        verify(interviewRepository).findById(interviewId);
        verify(candidateMapper).candidateToCandidateDTO(candidate);
        verify(offerMapper).toDto(offer);
    }

    @Test
    void should_throw_not_found_Exception(){

        UUID interviewId = UUID.randomUUID();

        when(interviewRepository.findById(interviewId)).thenReturn(Optional.empty());


        assertThatThrownBy(()->interviewServ.getInterviewEvaluationPlaceholders(interviewId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Interview not found with id: "+interviewId);

        verify(interviewRepository).findById(interviewId);
        verify(candidateMapper,never()).candidateToCandidateDTO(any());
        verify(offerMapper,never()).toDto(any());

    }

    @Test
    void getAllInterviewList_should_return_interview_List(){
        Interview interview = new Interview();
        interview.setId(UUID.randomUUID());
        interview.setDescription("nothing");
        interview.setNumberOfQuestions(15);
        when(interviewRepository.findAll()).thenReturn(List.of(interview));

        InterviewListDTO interviewList = new InterviewListDTO(
                interview.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "",
                null,
                null
        );

        when(interviewMapper.fromInterviewToInterviewListDTO(interview)).thenReturn(interviewList);

        List<InterviewListDTO> interviewListDTOS = interviewServ.getAllInterviewList();

        assertEquals(1, interviewListDTOS.size());
        assertTrue(interviewListDTOS.contains(interviewList));
        verify(interviewRepository).findAll();
        verify(interviewMapper).fromInterviewToInterviewListDTO(interview);
    }

    @Test
    void should_return_saved_evaluations() throws Exception {
        UUID interviewId = UUID.randomUUID();

        QuestionsAndAnswersForEvaluationDTO questionsAndAnswersForEvaluation1 =
                new QuestionsAndAnswersForEvaluationDTO(
                        "what is the useState hook",
                        "use state is",
                        2,
                        3);
        QuestionsAndAnswersForEvaluationDTO questionsAndAnswersForEvaluation2 =
                new QuestionsAndAnswersForEvaluationDTO(
                        "what is the useEffect hook",
                        "useEffect is",
                        2,
                        3);

        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),InterviewEvaluationPromptConstants.INTERVIEW_EVALUATION_PROMPT_CODE,InterviewEvaluationPromptConstants.INTERVIEW_EVALUATION_PROMPT,InterviewEvaluationPromptConstants.JSON_SCHEMA);

        InterviewEvaluationsRequestDTO interviewEvaluationsRequest =
                new InterviewEvaluationsRequestDTO(List.of(questionsAndAnswersForEvaluation1, questionsAndAnswersForEvaluation2));

        Evaluation evaluation = new Evaluation();
        evaluation.setFeedback("nothing");
        evaluation.setScore(60.0);

        EvaluationType evaluationType1 = new EvaluationType();
        evaluationType1.setId(UUID.randomUUID());
        evaluationType1.setDescription("React Skills");
        evaluationType1.setCoefficient(3.0);

        EvaluationType evaluationType2 = new EvaluationType();
        evaluationType2.setId(UUID.randomUUID());
        evaluationType2.setDescription("Java Skills");
        evaluationType2.setCoefficient(2.0);

        List<EvaluationType> evaluationTypes = List.of(evaluationType1, evaluationType2);

        CandidateDTO candidateDTO = new CandidateDTO(
                UUID.randomUUID(),
                "John",
                null,
                10,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        OfferDTO offerDTO = new OfferDTO(
                UUID.randomUUID(),
                "Backend Developer",
                "just testing",
                null,
                null
        );

        PlaceholdersForInterviewEvaluationPromptDTO placeholders = new PlaceholdersForInterviewEvaluationPromptDTO(candidateDTO, offerDTO, evaluationTypes);


        List<EvaluationsAIResponseDTO> aiEvaluationResponse = List.of(
                new EvaluationsAIResponseDTO(4.0, "Good understanding of hooks", "react skills"),
                new EvaluationsAIResponseDTO(3.5, "Basic understanding of lifecycle", "react skills")
        );

        List<Evaluation> existingEvaluations = List.of(evaluation);
        List<Evaluation> savedEvaluations = List.of(evaluation);
        InterviewServImpl interviewServSpy = Mockito.spy(interviewServ);

        doReturn(placeholders).when(interviewServSpy).getInterviewEvaluationPlaceholders(interviewId);
        when(promptServ.findByPromptCode(InterviewEvaluationPromptConstants.INTERVIEW_EVALUATION_PROMPT_CODE))
                .thenReturn(prompt);
        when(evaluationServ.prepareEvaluationsDTOFromAiResponse(eq(interviewEvaluationsRequest), eq(placeholders),eq(prompt))).thenReturn(aiEvaluationResponse);
        when(evaluationServ.getAllEvaluationsByInterviewID(interviewId)).thenReturn(existingEvaluations);
        when(evaluationServ.saveAIEvaluationResponse(aiEvaluationResponse, existingEvaluations)).thenReturn(savedEvaluations);

        List<Evaluation> result = interviewServSpy.prepareInterviewEvaluations(interviewId, interviewEvaluationsRequest);


        assertEquals(savedEvaluations, result);
        verify(interviewServSpy).getInterviewEvaluationPlaceholders(interviewId);
        verify(evaluationServ).prepareEvaluationsDTOFromAiResponse(interviewEvaluationsRequest, placeholders, prompt);
        verify(evaluationServ).getAllEvaluationsByInterviewID(interviewId);
        verify(evaluationServ).saveAIEvaluationResponse(aiEvaluationResponse, existingEvaluations);
    }


    @Test
    void should_return_interview_evaluations() throws Exception {

        UUID interviewId = UUID.randomUUID();

        EvaluationTypeDTO evaluationTypeDTO = new EvaluationTypeDTO(
                UUID.randomUUID(),
                "just testing",
                3.0
        );

        FullEvaluationDTO fullEvaluationDTO = new FullEvaluationDTO(
                UUID.randomUUID(),
                60.0,
                "just testing",
                interviewId,
                evaluationTypeDTO
        );

        LocalDateTime dateTime = LocalDateTime.now();
        InterviewEvaluationDTO interviewEvaluation = new InterviewEvaluationDTO(
                interviewId,
                "test test",
                "backend dev",
                dateTime,
                50,
                List.of(fullEvaluationDTO)
        );
        Interview interview = new Interview();
        interview.setId(interviewId);
        interview.setScheduledAt(dateTime);

        EvaluationType evaluationType = new EvaluationType();
        evaluationType.setId(UUID.randomUUID());
        evaluationType.setDescription("nothing");
        evaluationType.setCoefficient(3.0);

        Evaluation evaluation = new Evaluation(
                UUID.randomUUID(),
                60.0,
                "just testing",
                interview,
                evaluationType
        );

        when(evaluationServ.getAllEvaluationsByInterviewID(interviewId)).thenReturn(List.of(evaluation));

        when(evaluationMapper.mapEvaluationToInterviewEvaluation(List.of(evaluation)))
                .thenReturn(interviewEvaluation);

        InterviewEvaluationDTO result = interviewServ.getInterviewEvaluations(interviewId);

        assertThat(result.scheduledAt()).isEqualTo(interview.getScheduledAt());

        verify(evaluationServ).getAllEvaluationsByInterviewID(interviewId);
        verify(evaluationMapper).mapEvaluationToInterviewEvaluation(List.of(evaluation));
    }


    @Test
    void should_add_comment_to_interview(){
        UUID interviewId = UUID.randomUUID();
        String comment = "This is a new comment";

        Interview interview = new Interview();
        interview.setId(interviewId);
        interview.setComment("Old comment");

        Interview updatedInterview = new Interview();
        updatedInterview.setId(interviewId);
        updatedInterview.setComment(comment);

        InterviewDTO interviewDTO = new InterviewDTO(
                interviewId,
                null,
                null,
                null,
                null,
                null,
                null,
                comment,
                0,
                0,
                null,
                null,
                null,
                null
        );

        when(interviewRepository.findById(interviewId)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(any(Interview.class))).thenReturn(updatedInterview);
        when(interviewMapper.toDto(updatedInterview)).thenReturn(interviewDTO);

        InterviewDTO result = interviewServ.addCommentToInterview(interviewId, comment);

        assertThat(result).isNotNull();
        assertThat(result.comment()).isEqualTo(comment);

        verify(interviewRepository).findById(interviewId);
        verify(interviewRepository).save(interview);
        verify(interviewMapper).toDto(updatedInterview);
    }

    @Test
    void addCommentToInterview_shouldThrowException_whenInterviewNotFound() {
        UUID interviewId = UUID.randomUUID();

        when(interviewRepository.findById(interviewId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interviewServ.addCommentToInterview(interviewId, "Any comment"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Interview not found with ID");

        verify(interviewRepository).findById(interviewId);
        verifyNoMoreInteractions(interviewRepository, interviewMapper);
    }

    @Test
    void should_save_interview_link(){
        Interview interview = new Interview();
        interview.setId(UUID.randomUUID());
        String token = "abc";
        when(interviewRepository.findById(interview.getId())).thenReturn(Optional.of(interview));

        when(interviewRepository.save(any(Interview.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = interviewServ.saveInterviewLink(token, interview.getId());


        assertEquals(TEST_BASE_LINK + token, result);
        assertEquals(TEST_BASE_LINK + token, interview.getLink());
        verify(interviewRepository).save(interview);
    }

    @Test
    void save_interview_link_should_raise_an_error(){
        UUID interviewId = UUID.randomUUID();

        when(interviewRepository.findById(interviewId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interviewServ.saveInterviewLink(TEST_BASE_LINK, interviewId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Interview not found with id: "+interviewId);
    }

    @Test
    void should_return_email_info_when_email_exists() {
        UUID interviewId = UUID.randomUUID();
        Candidate candidate = new Candidate();
        candidate.setFullName("John Doe");
        Contact emailContact = new Contact();
        emailContact.setContactType("email");
        emailContact.setContactValue("john@example.com");
        candidate.setContacts(List.of(emailContact));

        Offer offer = new Offer();
        offer.setTitle("Software Engineer");

        Interview interview = new Interview();
        interview.setId(interviewId);
        interview.setCandidate(candidate);
        interview.setOffer(offer);
        interview.setScheduledAt(LocalDateTime.of(2025, 8, 27, 10, 0));
        interview.setLink(TEST_BASE_LINK+"abc");

        when(interviewRepository.findWithCandidateAndOfferById(interviewId))
                .thenReturn(Optional.of(interview));

        SendEmailDTO dto = interviewServ.getEmailInfo(interviewId);

        assertEquals("John Doe", dto.candidateFullName());
        assertEquals("Software Engineer", dto.offerTitle());
        assertEquals("john@example.com", dto.email());
        assertEquals(interview.getScheduledAt(), dto.scheduledDate());
        assertEquals(TEST_BASE_LINK+"abc", dto.link());
    }

    @Test
    void should_throw_exception_when_interview_not_found() {
        UUID interviewId = UUID.randomUUID();
        when(interviewRepository.findWithCandidateAndOfferById(interviewId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> interviewServ.getEmailInfo(interviewId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Interview not found with id: "+interviewId);
    }

    @Test
    void should_throw_exception_when_candidate_email_not_found() {
        UUID interviewId = UUID.randomUUID();
        Candidate candidate = new Candidate();
        candidate.setFullName("John Doe");
        candidate.setContacts(Collections.emptyList());

        Offer offer = new Offer();
        offer.setTitle("Software Engineer");

        Interview interview = new Interview();
        interview.setId(interviewId);
        interview.setCandidate(candidate);
        interview.setOffer(offer);

        when(interviewRepository.findWithCandidateAndOfferById(interviewId))
                .thenReturn(Optional.of(interview));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> interviewServ.getEmailInfo(interviewId));
        assertEquals("Candidate email not found", ex.getMessage());
    }

    @Test
    void should_generate_and_save_questions() {
        UUID interviewId = UUID.randomUUID();
        List<UUID> evaluationTypeIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        GenerateInterviewQuestionsRequest request = new GenerateInterviewQuestionsRequest(evaluationTypeIds);

        ContactDTO contact = new ContactDTO(UUID.randomUUID(),"email", "john.doe@example.com");
        List<ContactDTO> contacts = List.of(contact);
        CandidateDTO candidate = new CandidateDTO(
                UUID.randomUUID(),
                "John Doe",
                LocalDate.of(1990, 1, 1),
                5,
                GenderEnum.M,
                "Java",
                "Experienced Java developer",
                contacts,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                Collections.emptyList(),
                Collections.emptyList()
        );

        OfferDTO offer = new OfferDTO(
                UUID.randomUUID(),
                "Java Developer",
                "Backend Java developer position",
                null,
                Collections.emptyList()
        );

        placeholdersForInterviewQuestionsPromptDTO placeholders = new placeholdersForInterviewQuestionsPromptDTO(
                candidate,
                offer,
                5,
                60
        );
        List<EvaluationTypeDTO> evaluationTypes = List.of(
                new EvaluationTypeDTO(UUID.randomUUID(), "Technical",2.0),
                new EvaluationTypeDTO(UUID.randomUUID(), "Behavioral",3.0)
        );
        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),InterviewPromptConstants.INTERVIEW_GENERATE_QUESTIONS_PROMPT_CODE,
        InterviewPromptConstants.INTERVIEW_QUESTION_GENERATION_PROMPT,InterviewPromptConstants.JSON_SCHEMA);

        List<QuestionDTO> aiGeneratedQuestions = List.of(
                new QuestionDTO(null, "Question 1", 10, null, null),
                new QuestionDTO(null, "Question 2", 15, null, null)
        );

        List<QuestionDTO> savedQuestions = List.of(
                new QuestionDTO(UUID.randomUUID(), "Question 1", 10, interviewId, null),
                new QuestionDTO(UUID.randomUUID(), "Question 2", 15, interviewId, null)
        );

        InterviewServImpl spyService = spy(interviewServ);
        doReturn(placeholders).when(spyService).getPlaceholdersForInterviewQuestionsPrompt(interviewId);
        when(evaluationTypeServ.findAllById(evaluationTypeIds)).thenReturn(evaluationTypes);
        when(promptServ.findByPromptCode(InterviewPromptConstants.INTERVIEW_GENERATE_QUESTIONS_PROMPT_CODE)).thenReturn(prompt);
        when(questionServ.prepareQuestionsFromAIResponse(placeholders, evaluationTypes, prompt)).thenReturn(aiGeneratedQuestions);
        when(questionServ.saveAllQuestions(anyList())).thenReturn(savedQuestions);

        List<QuestionDTO> result = spyService.generateInterviewQuestions(interviewId, request);

        assertEquals(2, result.size());
        assertEquals(interviewId, result.get(0).interviewId());
        assertEquals("Question 1", result.get(0).description());
        assertEquals("Question 2", result.get(1).description());

        verify(spyService).getPlaceholdersForInterviewQuestionsPrompt(interviewId);
        verify(evaluationTypeServ).findAllById(evaluationTypeIds);
        verify(promptServ).findByPromptCode(InterviewPromptConstants.INTERVIEW_GENERATE_QUESTIONS_PROMPT_CODE);
        verify(questionServ).prepareQuestionsFromAIResponse(placeholders, evaluationTypes, prompt);
        verify(questionServ).saveAllQuestions(anyList());
    }
}
