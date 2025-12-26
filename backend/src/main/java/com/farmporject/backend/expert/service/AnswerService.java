package com.farmporject.backend.expert.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.farmporject.backend.expert.repository.ExpertRepository;
import com.farmporject.backend.expert.repository.AnswerRepository;

import com.farmporject.backend.expert.model.*;
import com.farmporject.backend.expert.dto.AnswerDTO;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final ExpertRepository expertRepository;
    private final QAService qaService;

    public AnswerService(AnswerRepository answerRepository, ExpertRepository expertRepository, QAService qaService) {
        this.answerRepository = answerRepository;
        this.expertRepository = expertRepository;
        this.qaService = qaService;
    }

    /**
     * 提交回答
     * 
     * @param answerDto
     * @return
     */
    public AnswerDTO createAnswer(AnswerDTO answerDto) {
        Long expert_id = answerDto.getExpertId();
        if (expert_id == null) {
            expert_id = 1L;
        }
        List<Expert> experts = expertRepository.findByUser_Id(expert_id);
        if (experts.isEmpty()) {
            throw new RuntimeException("Expert not found with user id: " + expert_id);
        }
        Expert expert = experts.get(0);
        Question question = qaService.getQuestionById(answerDto.getQuestionId()).get();

        // 修改question的status
        if (question.getStatus() == Question.QuestionStatus.PENDING) {
            qaService.updateQuestionStatus(answerDto.getQuestionId(), Question.QuestionStatus.ANSWERED);
        } else if (question.getStatus() == Question.QuestionStatus.CLOSED) {
            // 问题已关闭，不能回答
            return null;
        }

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setExpert(expert);
        answer.setContent(answerDto.getContent());
        answer.setCreateTime(LocalDateTime.now());
        answer.setViewCount(0);
        answer.setLikeCount(0);

        answer = answerRepository.save(answer);
        if (answer.getId() != null) {
            answerDto.setExpertName(expert.getName());
            answerDto.setId(answer.getId());
            return answerDto;
        }
        return null;
    }

    /**
     * 获取最近的10条回答
     * 
     * @return
     */
    public List<AnswerDTO> getRecentAnswers() {
        return answerRepository.findAll().stream()
                .sorted((a1, a2) -> a2.getCreateTime().compareTo(a1.getCreateTime()))
                .limit(5)
                .map(answer -> {
                    AnswerDTO dto = new AnswerDTO(answer.getQuestion().getId(), answer.getExpert().getId(),
                            answer.getContent());
                    dto.setId(answer.getId());
                    dto.setExpertName(answer.getExpert().getName());
                    dto.setCreateTime(answer.getCreateTime());
                    dto.setViewCount(answer.getViewCount());
                    dto.setLikeCount(answer.getLikeCount());
                    dto.setQuestionTitle(answer.getQuestion().getTitle());
                    return dto;
                }).toList();
    }

    /**
     * 点赞该条回答
     * 
     * @param answerId
     * @return
     */
    public boolean likeAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId).get();
        answer.setLikeCount(answer.getLikeCount() + 1);
        answerRepository.save(answer);
        return true;
    }
}
