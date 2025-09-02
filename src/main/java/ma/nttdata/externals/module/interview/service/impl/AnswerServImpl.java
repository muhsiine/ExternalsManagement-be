package ma.nttdata.externals.module.interview.service.impl;

import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.AnswerDTO;
import ma.nttdata.externals.module.interview.dto.CreateAnswerForQuestionDTO;
import ma.nttdata.externals.module.interview.entity.Answer;
import ma.nttdata.externals.module.interview.entity.Question;
import ma.nttdata.externals.module.interview.mapper.AnswerMapper;
import ma.nttdata.externals.module.interview.repository.AnswerRepository;
import ma.nttdata.externals.module.interview.repository.QuestionRepository;
import ma.nttdata.externals.module.interview.service.AnswerServ;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerServImpl implements AnswerServ {

    private final AnswerRepository answerRepository;
    private final AnswerMapper answerMapper;
    private final QuestionRepository questionRepository;

    @Override
    public List<AnswerDTO> getAllAnswers() {
        return answerMapper.toDtoList(answerRepository.findAll());
    }

    @Override
    public AnswerDTO getAnswerById(UUID id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));
        return answerMapper.toDto(answer);
    }

    @Override
    public AnswerDTO createAnswer(AnswerDTO answerDTO) {
        Answer answer = answerMapper.toEntity(answerDTO);

        Answer saved = answerRepository.save(answer);
        return answerMapper.toDto(saved);
    }

    @Override
    public AnswerDTO updateAnswer(UUID id, AnswerDTO answerDTO) {
        Answer existing = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));

        existing.setDescription(answerDTO.description());
        existing.setDurationInMinutes(answerDTO.durationInMinutes());

        Answer updated = answerRepository.save(existing);
        return answerMapper.toDto(updated);
    }

    @Override
    public void deleteAnswer(UUID id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));
        answerRepository.delete(answer);
    }

    @Override
    @Transactional
    public AnswerDTO createAnswerForQuestion(CreateAnswerForQuestionDTO createAnswerDTO) {
        // Find the question first
        Question question = questionRepository.findById(createAnswerDTO.questionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + createAnswerDTO.questionId()));

        // Check if question already has an answer
        if (question.getAnswer() != null) {
            throw new IllegalStateException("Question already has an answer with id: " + question.getAnswer().getId());
        }

        Answer answer = new Answer();
        answer.setDescription(createAnswerDTO.description());
        answer.setDurationInMinutes(createAnswerDTO.durationInMinutes());

        Answer savedAnswer = answerRepository.save(answer);

        question.setAnswer(savedAnswer);
        questionRepository.save(question);

        return answerMapper.toDto(savedAnswer);
    }
}