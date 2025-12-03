package com.farmporject.backend.expert.service;

import com.farmporject.backend.expert.model.Answer;
import com.farmporject.backend.expert.model.Expert;
import com.farmporject.backend.expert.model.Question;
import com.farmporject.backend.expert.repository.AnswerRepository;
import com.farmporject.backend.expert.repository.ExpertRepository;
import com.farmporject.backend.expert.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QAService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ExpertRepository expertRepository;

    @Autowired
    public QAService(QuestionRepository questionRepository, AnswerRepository answerRepository,
            ExpertRepository expertRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.expertRepository = expertRepository;
    }

    // Question methods
    public List<Question> getAllQuestions() {
        // 查询所有问题，然后在内存中排序
        List<Question> list = questionRepository.findAll();

        // 初始化懒加载的集合，避免 LazyInitializationException
        for (Question q : list) {
            try {
                if (q.getAttachmentUrls() != null) {
                    q.getAttachmentUrls().size(); // 触发懒加载
                }
            } catch (Exception e) {
                // 如果懒加载失败，设置为空列表
                q.setAttachmentUrls(new java.util.ArrayList<>());
            }
            try {
                if (q.getTags() != null) {
                    q.getTags().size(); // 触发懒加载
                }
            } catch (Exception e) {
                // 如果懒加载失败，设置为空列表
                q.setTags(new java.util.ArrayList<>());
            }
        }

        // 按更新时间降序排列（如果updateTime为null，使用createTime）
        list.sort((a, b) -> {
            LocalDateTime timeA = a.getUpdateTime() != null ? a.getUpdateTime() : a.getCreateTime();
            LocalDateTime timeB = b.getUpdateTime() != null ? b.getUpdateTime() : b.getCreateTime();
            if (timeA == null && timeB == null)
                return 0;
            if (timeA == null)
                return 1;
            if (timeB == null)
                return -1;
            return timeB.compareTo(timeA);
        });
        return list;
    }

    public List<Question> getRecentQuestions() {
        return questionRepository.findTop10ByOrderByCreateTimeDesc();
    }

    public List<Question> getUserQuestions(Long userId) {
        // 查询用户问题，然后在内存中排序
        List<Question> list = questionRepository.findByUserId(userId);

        // 初始化懒加载的集合，避免 LazyInitializationException
        for (Question q : list) {
            try {
                if (q.getAttachmentUrls() != null) {
                    q.getAttachmentUrls().size(); // 触发懒加载
                }
            } catch (Exception e) {
                // 如果懒加载失败，设置为空列表
                q.setAttachmentUrls(new java.util.ArrayList<>());
            }
            try {
                if (q.getTags() != null) {
                    q.getTags().size(); // 触发懒加载
                }
            } catch (Exception e) {
                // 如果懒加载失败，设置为空列表
                q.setTags(new java.util.ArrayList<>());
            }
        }

        // 按更新时间降序排列（如果updateTime为null，使用createTime）
        list.sort((a, b) -> {
            LocalDateTime timeA = a.getUpdateTime() != null ? a.getUpdateTime() : a.getCreateTime();
            LocalDateTime timeB = b.getUpdateTime() != null ? b.getUpdateTime() : b.getCreateTime();
            if (timeA == null && timeB == null)
                return 0;
            if (timeA == null)
                return 1;
            if (timeB == null)
                return -1;
            return timeB.compareTo(timeA);
        });
        return list;
    }

    public List<Question> getExpertQuestions(Long expertId) {
        return questionRepository.findByExpertIdOrderByCreateTimeDesc(expertId);
    }

    public List<Question> getQuestionsByStatus(Question.QuestionStatus status) {
        return questionRepository.findByStatusOrderByCreateTimeDesc(status);
    }

    public List<Question> getUserQuestionsByStatus(Long userId, Question.QuestionStatus status) {
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
            // 先加载问题，清空关联的集合（附件和标签）
            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

            // 清空附件和标签，避免外键约束错误
            question.setAttachmentUrls(new java.util.ArrayList<>());
            question.setTags(new java.util.ArrayList<>());
            questionRepository.save(question); // 保存以删除关联记录

            // 先删除相关的回答
            List<Answer> answers = answerRepository.findByQuestionId(id);
            answerRepository.deleteAll(answers);

            // 最后删除问题本身
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

    /**
     * 获取问题的答案数量（不加载答案实体，只查询数量）
     * // 查询用户问题，然后在内存中排序
     * List<Question> list = questionRepository.findByUserId(userId);
     * 
     * // 初始化懒加载的集合，避免 LazyInitializationException
     * for (Question q : list) {
     * try {
     * if (q.getAttachmentUrls() != null) {
     * q.getAttachmentUrls().size(); // 触发懒加载
     * }
     * } catch (Exception e) {
     * // 如果懒加载失败，设置为空列表
     * q.setAttachmentUrls(new java.util.ArrayList<>());
     * }
     * try {
     * if (q.getTags() != null) {
     * q.getTags().size(); // 触发懒加载
     * }
     * } catch (Exception e) {
     * // 如果懒加载失败，设置为空列表
     * q.setTags(new java.util.ArrayList<>());
     * }
     * }
     * 
     * // 按更新时间降序排列（如果updateTime为null，使用createTime）
     * list.sort((a, b) -> {
     * LocalDateTime timeA = a.getUpdateTime() != null ? a.getUpdateTime() :
     * a.getCreateTime();
     * LocalDateTime timeB = b.getUpdateTime() != null ? b.getUpdateTime() :
     * b.getCreateTime();
     * if (timeA == null && timeB == null) return 0;
     * if (timeA == null) return 1;
     * if (timeB == null) return -1;
     * return timeB.compareTo(timeA);
     * });
     * return list;
     * }
     * 
     * public List<Question> getExpertQuestions(Long expertId) {
     * return questionRepository.findByExpertIdOrderByCreateTimeDesc(expertId);
     * }
     * 
     * public List<Question> getQuestionsByStatus(Question.QuestionStatus status) {
     * return questionRepository.findByStatusOrderByCreateTimeDesc(status);
     * }
     * 
     * public List<Question> getUserQuestionsByStatus(String userId,
     * Question.QuestionStatus status) {
     * return questionRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId,
     * status);
     * }
     * 
     * public List<Question> searchQuestionsByKeyword(String keyword) {
     * return questionRepository.findByKeyword(keyword);
     * }
     * 
     * public List<Question> searchQuestionsByTag(String tag) {
     * return questionRepository.findByTagsContaining(tag);
     * }
     * 
     * public Optional<Question> getQuestionById(Long id) {
     * return questionRepository.findById(id);
     * }
     * 
     * public Question createQuestion(Question question) {
     * // 设置默认状态
     * if (question.getStatus() == null) {
     * question.setStatus(Question.QuestionStatus.PENDING);
     * }
     * 
     * return questionRepository.save(question);
     * }
     * 
     * public Question updateQuestionAttachments(Long id, List<String>
     * attachmentUrls) {
     * return questionRepository.findById(id)
     * .map(question -> {
     * question.setAttachmentUrls(attachmentUrls);
     * return questionRepository.save(question);
     * })
     * .orElseThrow(() -> new RuntimeException("Question not found with id: " +
     * id));
     * }
     * 
     * public Question updateQuestionStatus(Long id, Question.QuestionStatus status)
     * {
     * return questionRepository.findById(id)
     * .map(question -> {
     * question.setStatus(status);
     * return questionRepository.save(question);
     * })
     * .orElseThrow(() -> new RuntimeException("Question not found with id: " +
     * id));
     * }
     * 
     * public Question updateQuestion(Long id, Question questionDetails) {
     * return questionRepository.findById(id)
     * .map(question -> {
     * if (questionDetails.getTitle() != null) {
     * question.setTitle(questionDetails.getTitle());
     * }
     * if (questionDetails.getContent() != null) {
     * question.setContent(questionDetails.getContent());
     * }
     * if (questionDetails.getTags() != null) {
     * question.setTags(questionDetails.getTags());
     * }
     * if (questionDetails.getStatus() != null) {
     * question.setStatus(questionDetails.getStatus());
     * }
     * return questionRepository.save(question);
     * })
     * .orElseThrow(() -> new RuntimeException("Question not found with id: " +
     * id));
     * }
     * 
     * public void deleteQuestion(Long id) {
     * if (questionRepository.existsById(id)) {
     * // 先加载问题，清空关联的集合（附件和标签）
     * Question question = questionRepository.findById(id)
     * .orElseThrow(() -> new RuntimeException("Question not found with id: " +
     * id));
     * 
     * // 清空附件和标签，避免外键约束错误
     * question.setAttachmentUrls(new java.util.ArrayList<>());
     * question.setTags(new java.util.ArrayList<>());
     * questionRepository.save(question); // 保存以删除关联记录
     * 
     * // 先删除相关的回答
     * List<Answer> answers = answerRepository.findByQuestionId(id);
     * answerRepository.deleteAll(answers);
     * 
     * // 最后删除问题本身
     * questionRepository.deleteById(id);
     * } else {
     * throw new RuntimeException("Question not found with id: " + id);
     * }
     * }
     * 
     * // Answer methods
     * public List<Answer> getQuestionAnswers(Long questionId) {
     * return answerRepository.findByQuestionIdOrderByCreateTime(questionId);
     * }
     * 
     * public List<Answer> getExpertAnswers(Long expertId) {
     * return answerRepository.findByExpertIdOrderByCreateTimeDesc(expertId);
     * }
     * 
     * public List<Answer> getAnsweredQuestionsByExpert(Long expertId) {
     * return answerRepository.findAnsweredQuestionsByExpertId(expertId);
     * }
     * 
     * public List<Answer> searchAnswersByContent(String keyword) {
     * return answerRepository.findByContentContaining(keyword);
     * }
     * 
     * public Answer createAnswer(Answer answer) {
     * // 验证问题是否存在
     * Question question = questionRepository.findById(answer.getQuestion().getId())
     * .orElseThrow(() -> new RuntimeException("Question not found with id: " +
     * answer.getQuestion().getId()));
     * 
     * // 更新问题状态为已回答
     * question.setStatus(Question.QuestionStatus.ANSWERED);
     * questionRepository.save(question);
     * 
     * return answerRepository.save(answer);
     * }
     * 
     * public Optional<Answer> getAnswerById(Long id) {
     * return answerRepository.findById(id);
     * }
     * 
     * public Answer updateAnswer(Long id, Answer answerDetails) {
     * return answerRepository.findById(id)
     * .map(answer -> {
     * if (answerDetails.getContent() != null) {
     * answer.setContent(answerDetails.getContent());
     * }
     * return answerRepository.save(answer);
     * })
     * .orElseThrow(() -> new RuntimeException("Answer not found with id: " + id));
     * }
     * 
     * public void deleteAnswer(Long id) {
     * if (answerRepository.existsById(id)) {
     * answerRepository.deleteById(id);
     * } else {
     * throw new RuntimeException("Answer not found with id: " + id);
     * }
     * }
     * 
     * public long countAnswersByExpert(Long expertId) {
     * return answerRepository.countByExpertId(expertId);
     * }
     * 
     * public long countQuestionsByExpertAndStatus(Long expertId,
     * Question.QuestionStatus status) {
     * return questionRepository.countByExpertIdAndStatus(expertId, status);
     * }
     * 
     * public boolean questionHasAnswer(Long questionId) {
     * List<Answer> answers = answerRepository.findByQuestionId(questionId);
     * return !answers.isEmpty();
     * }
     * 
     * /**
     * 获取问题的答案数量（不加载答案实体，只查询数量）
     */
    public int getQuestionAnswersCount(Long questionId) {
        return answerRepository.findByQuestionId(questionId).size();
    }

    /**
     * 获取所有专家列表
     */
    public List<Expert> getAllExperts() {
        return expertRepository.findAll();
    }

    // Question view and like methods
    public void viewQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("Question not found with id: " + id);
        }
        questionRepository.incrementViewCount(id);
    }

    public void likeQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("Question not found with id: " + id);
        }
        questionRepository.incrementLikeCount(id);
    }

    public void unlikeQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("Question not found with id: " + id);
        }
        questionRepository.decrementLikeCount(id);
    }

    // Answer view and like methods
    public void viewAnswer(Long id) {
        if (!answerRepository.existsById(id)) {
            throw new RuntimeException("Answer not found with id: " + id);
        }
        answerRepository.incrementViewCount(id);
    }

    public void likeAnswer(Long id) {
        if (!answerRepository.existsById(id)) {
            throw new RuntimeException("Answer not found with id: " + id);
        }
        answerRepository.incrementLikeCount(id);
    }

    public void unlikeAnswer(Long id) {
        if (!answerRepository.existsById(id)) {
            throw new RuntimeException("Answer not found with id: " + id);
        }
        answerRepository.decrementLikeCount(id);
    }
}