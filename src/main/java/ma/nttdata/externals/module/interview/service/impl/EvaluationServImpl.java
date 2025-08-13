package ma.nttdata.externals.module.interview.service.impl;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.commons.constants.InterviewEvaluationPromptConstants;
import ma.nttdata.externals.commons.exception.InternalServerException;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.candidate.dto.CandidateWithoutInterviewDTO;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.mapper.EvaluationMapper;
import ma.nttdata.externals.module.interview.repository.EvaluationRepository;
import ma.nttdata.externals.module.interview.repository.EvaluationTypeRepository;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
import ma.nttdata.externals.module.interview.service.EvaluationServ;
import ma.nttdata.externals.module.offer.dto.OfferWithoutInterviewDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import java.util.List;
import java.util.UUID;

@Service
public class EvaluationServImpl implements EvaluationServ {

    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;
    private final InterviewRepository interviewRepository;
    private final EvaluationTypeRepository evaluationTypeRepository;
    private final boolean mockFlag;
    private final RestClient aiRestClient;
    private final CandidateMapper candidateMapper;


    public EvaluationServImpl(EvaluationRepository evaluationRepository,
                              EvaluationMapper evaluationMapper,
                              InterviewRepository interviewRepository,
                              CandidateMapper candidateMapper,

                              EvaluationTypeRepository evaluationTypeRepository,
                              @Value("${app.mock.flag}") boolean mockFlag,
                              @Qualifier("aiServiceClient") RestClient aiRestClient) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationMapper = evaluationMapper;
        this.interviewRepository = interviewRepository;
        this.evaluationTypeRepository = evaluationTypeRepository;
        this.mockFlag = mockFlag;
        this.aiRestClient = aiRestClient;
        this.candidateMapper = candidateMapper; // Add this

    }

    @Override
    public List<EvaluationDTO> getAllEvaluations() {
        return evaluationMapper.toDtoList(evaluationRepository.findAll());
    }

    @Override
    public EvaluationDTO getEvaluationById(UUID id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with id: " + id));
        return evaluationMapper.toDto(evaluation);
    }

    @Override
    public EvaluationDTO createEvaluation(EvaluationDTO evaluationDTO) {
        Evaluation evaluation = evaluationMapper.toEntity(evaluationDTO);

        Interview interview = interviewRepository.findById(evaluationDTO.interviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + evaluationDTO.interviewId()));
        evaluation.setInterview(interview);

        EvaluationType evaluationType = evaluationTypeRepository.findById(evaluationDTO.evaluationTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationType not found with id: " + evaluationDTO.evaluationTypeId()));
        evaluation.setEvaluationType(evaluationType);

        Evaluation saved = evaluationRepository.save(evaluation);
        return evaluationMapper.toDto(saved);
    }

    @Override
    public EvaluationDTO updateEvaluation(UUID id, EvaluationDTO evaluationDTO) {
        Evaluation existing = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with id: " + id));

        existing.setFeedback(evaluationDTO.feedback());
        existing.setScore(evaluationDTO.score());

        Interview interview = interviewRepository.findById(evaluationDTO.interviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + evaluationDTO.interviewId()));
        existing.setInterview(interview);

        EvaluationType evaluationType = evaluationTypeRepository.findById(evaluationDTO.evaluationTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationType not found with id: " + evaluationDTO.evaluationTypeId()));
        existing.setEvaluationType(evaluationType);

        Evaluation updated = evaluationRepository.save(existing);
        return evaluationMapper.toDto(updated);
    }

    @Override
    public void deleteEvaluation(UUID id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with id: " + id));
        evaluationRepository.delete(evaluation);
    }

    @Override
    public List<EvaluationsAIResponseDTO> prepareEvaluationsDTOFromAiResponse(InterviewEvaluationsRequestDTO interviewEvaluationsRequest,PlaceholdersForInterviewEvaluationPromptDTO  placeholders){
        try{
            String generatedEvaluation = mockFlag ? InterviewEvaluationPromptConstants.JSON_MOCK
                    :getInterviewsEvaluationsFromAiByPrompt(interviewEvaluationsRequest,placeholders);

            ObjectMapper objectMapper = new ObjectMapper();
            List<EvaluationsAIResponseDTO> evaluation = objectMapper.readValue(generatedEvaluation, new TypeReference<List<EvaluationsAIResponseDTO>>() {});

            return evaluation;
        }catch (Exception e){
            throw new InternalServerException("Failed to parse questions JSON", e);
        }

    }

    @Override
    public String getInterviewsEvaluationsFromAiByPrompt(InterviewEvaluationsRequestDTO interviewEvaluationsRequest, PlaceholdersForInterviewEvaluationPromptDTO placeholders){
        String prompt  = InterviewEvaluationPromptConstants.INTERVIEW_EVALUATION_PROMPT;

        prompt.replace(InterviewEvaluationPromptConstants.CANDIDATE_PLACEHOLDER,placeholders.candidate().toString())
                .replace(InterviewEvaluationPromptConstants.OFFER_PLACEHOLDER,placeholders.offer().toString())
                .replace(InterviewEvaluationPromptConstants.JSON_SCHEMA_PLACEHOLDER,InterviewEvaluationPromptConstants.JS0N_SCHEMA)
                .replace(InterviewEvaluationPromptConstants.QUESTION_ANSWER_DTO_PLACEHOLDER,interviewEvaluationsRequest.questionsAndAnswersForEvaluation().toString())
                .replace(InterviewEvaluationPromptConstants.EVALUATION_TYPES_PLACEHOLDER,placeholders.evaluationType().toString())
                .replace(InterviewEvaluationPromptConstants.JSON_MOCK_PLACEHOLDER,InterviewEvaluationPromptConstants.JSON_MOCK_PLACEHOLDER);

        return aiRestClient.post()
                .uri("/evaluationInterview")
                .body(prompt)
                .retrieve()
                .body(String.class);
    }

    @Override
    public List<Evaluation> saveAIEvaluationResponse(List<EvaluationsAIResponseDTO> aiEvaluationsResponse, List<Evaluation> evaluations){
        List<Evaluation> evaluationsWithFeedbackAndScore = evaluationMapper.mapAIEvaluationResponsesToEvaluations(aiEvaluationsResponse,evaluations);

        return evaluationRepository.saveAll(evaluationsWithFeedbackAndScore);
    }

    @Override
    public List<Evaluation> getAllEvaluationsByInterviewID(UUID interviewId){

        return evaluationRepository.findByInterviewId(interviewId);
    }
    @Override
    public List<EvaluationWithInterviewAndEvaluationTypeDTO> getAllEvaluationsWithInterviewByInterviewId(UUID interviewId) {
        List<Evaluation> evaluations = evaluationRepository.findByInterviewId(interviewId);

        return evaluations.stream().map(evaluation -> {
            Interview interview = evaluation.getInterview();
            Candidate candidate = interview.getCandidate();

            CandidateDTO candidateDTO = candidateMapper.candidateToCandidateDTO(candidate);

            CandidateWithoutInterviewDTO candidateWithoutInterviewDTO = new CandidateWithoutInterviewDTO(
                    candidateDTO.id(),
                    candidateDTO.fullName(),
                    candidateDTO.birthDate(),
                    candidateDTO.yearsOfExperience(),
                    candidateDTO.gender(),
                    candidateDTO.mainTech(),
                    candidateDTO.summary(),
                    candidateDTO.contacts(),
                    candidateDTO.experiences(),
                    candidateDTO.skills(),
                    candidateDTO.educations(),
                    candidateDTO.cvFiles(),
                    candidateDTO.address(),
                    candidateDTO.naturalLanguages()
            );

            InterviewWithCandidateAndOfferDTO interviewDTO = new InterviewWithCandidateAndOfferDTO(
                    interview.getId(),
                    interview.getStartTime(),
                    interview.getEndTime(),
                    interview.getDescription(),
                    interview.getLink(),
                    interview.getFeedback_general(),
                    interview.getScheduledAt(),
                    interview.getComment(),
                    interview.getNumberOfQuestions(),
                    interview.getEstimatedDuration(),
                    candidateWithoutInterviewDTO,
                    new OfferWithoutInterviewDTO(
                            interview.getOffer().getId(),
                            interview.getOffer().getTitle(),
                            interview.getOffer().getDescription()
                    )
            );

            EvaluationType evaluationType = evaluation.getEvaluationType();
            EvaluationTypeDTO evaluationTypeDTO = new EvaluationTypeDTO(
                    evaluationType.getId(),
                    evaluationType.getDescription(),
                    evaluationType.getCoefficient()
            );

            return new EvaluationWithInterviewAndEvaluationTypeDTO(
                    evaluation.getId(),
                    evaluation.getScore(),
                    evaluation.getFeedback(),
                    interviewDTO,
                    evaluationTypeDTO
            );
        }).toList();
    }

}
