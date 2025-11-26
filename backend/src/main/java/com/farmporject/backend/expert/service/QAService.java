package com.farmporject.backend.expert.service;

import com.farmporject.backend.expert.model.Answer;
import com.farmporject.backend.expert.model.Question;
import com.farmporject.backend.expert.repository.AnswerRepository;
import com.farmporject.backend.expert.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QAService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public QAService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    // Question methods
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getRecentQuestions() {
        return questionRepository.findTop10ByOrderByCreateTimeDesc();
    }

    public List<Question> getUserQuestions(String userId) {
        return questionRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public List<Question> getExpertQuestions(Long expertId) {
        return questionRepository.findByExpertIdOrderByCreateTimeDesc(expertId);
    }

    public List<Question> getQuestionsByStatus(Question.QuestionStatus status) {
        return questionRepository.findByStatusOrderByCreateTimeDesc(status);
    }

    public List<Question> getUserQuestionsByStatus(String userId, Question.QuestionStatus status) {
        return questionRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId, status);
    }

    public List<Question> searchQuestionsByKeyword(String keyword) {
        return questionRepository.findByKeyword(keyword);
    }

    public List<Question> searchQuestionsByTag(String tag) {
        return questionRepository.findByTagsContaining(tag);
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public Question createQuestion(Question question) {
        // 设置默认状态
        if (question.getStatus() == null) {
            question.setStatus(Question.QuestionStatus.PENDING);
        }

        return questionRepository.save(question);
    }

    public Question updateQuestionAttachments(Long id, List<String> attachmentUrls) {
        return questionRepository.findById(id)
                .map(question -> {
                    question.setAttachmentUrls(attachmentUrls);
                    return questionRepository.save(question);
                })
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
    }

    public Question updateQuestionStatus(Long id, Question.QuestionStatus status) {
        return questionRepository.findById(id)
                .map(question -> {
                    question.setStatus(status);
                    return questionRepository.save(question);
                })
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
    }

    public Question updateQuestion(Long id, Question questionDetails) {
        return questionRepository.findById(id)
                .map(question -> {
                    if (questionDetails.getTitle() != null) {
                        question.setTitle(questionDetails.getTitle());
                    }
                    if (questionDetails.getContent() != null) {
                        question.setContent(questionDetails.getContent());
                    }
                    if (questionDetails.getTags() != null) {
                        question.setTags(questionDetails.getTags());
                    }
                    if (questionDetails.getStatus() != null) {
                        question.setStatus(questionDetails.getStatus());
                    }
                    return questionRepository.save(question);
                })
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
    }

    public void deleteQuestion(Long id) {
        if (questionRepository.existsById(id)) {
            // 先删除相关的回答
            List<Answer> answers = answerRepository.findByQuestionId(id);
            answerRepository.deleteAll(answers);

            questionRepository.deleteById(id);
        } else {
            throw new RuntimeException("Question not found with id: " + id);
        }
    }

    // Answer methods
    public List<Answer> getQuestionAnswers(Long questionId) {
        return answerRepository.findByQuestionIdOrderByCreateTime(questionId);
    }

    public List<Answer> getExpertAnswers(Long expertId) {
        return answerRepository.findByExpertIdOrderByCreateTimeDesc(expertId);
    }

    public List<Answer> getAnsweredQuestionsByExpert(Long expertId) {
        return answerRepository.findAnsweredQuestionsByExpertId(expertId);
    }

    public List<Answer> searchAnswersByContent(String keyword) {
        return answerRepository.findByContentContaining(keyword);
    }

    public Answer createAnswer(Answer answer) {
        // 验证问题是否存在
        Question question = questionRepository.findById(answer.getQuestion().getId())
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + answer.getQuestion().getId()));

        // 更新问题状态为已回答
        question.setStatus(Question.QuestionStatus.ANSWERED);
        questionRepository.save(question);

        return answerRepository.save(answer);
    }

    public Optional<Answer> getAnswerById(Long id) {
        return answerRepository.findById(id);
    }

    public Answer updateAnswer(Long id, Answer answerDetails) {
        return answerRepository.findById(id)
                .map(answer -> {
                    if (answerDetails.getContent() != null) {
                        answer.setContent(answerDetails.getContent());
                    }
                    return answerRepository.save(answer);
                })
                .orElseThrow(() -> new RuntimeException("Answer not found with id: " + id));
    }

    public void deleteAnswer(Long id) {
        if (answerRepository.existsById(id)) {
            answerRepository.deleteById(id);
        } else {
            throw new RuntimeException("Answer not found with id: " + id);
        }
    }

    public long countAnswersByExpert(Long expertId) {
        return answerRepository.countByExpertId(expertId);
    }

    public long countQuestionsByExpertAndStatus(Long expertId, Question.QuestionStatus status) {
        return questionRepository.countByExpertIdAndStatus(expertId, status);
    }

    public boolean questionHasAnswer(Long questionId) {
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        return !answers.isEmpty();
    }
}