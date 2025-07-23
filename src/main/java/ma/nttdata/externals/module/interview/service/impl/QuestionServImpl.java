package ma.nttdata.externals.module.interview.service.impl;

import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.entity.Question;
import ma.nttdata.externals.module.interview.mapper.QuestionMapper;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
import ma.nttdata.externals.module.interview.repository.QuestionRepository;
import ma.nttdata.externals.module.interview.service.QuestionServ;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionServImpl implements QuestionServ {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final InterviewRepository interviewRepository;

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
}
