package ma.nttdata.externals.module.interview.service.impl;

import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.mapper.EvaluationMapper;
import ma.nttdata.externals.module.interview.repository.EvaluationRepository;
import ma.nttdata.externals.module.interview.repository.EvaluationTypeRepository;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
import ma.nttdata.externals.module.interview.service.EvaluationServ;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvaluationServImpl implements EvaluationServ {

    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;
    private final InterviewRepository interviewRepository;
    private final EvaluationTypeRepository evaluationTypeRepository;

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
}
