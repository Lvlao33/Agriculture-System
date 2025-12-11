package com.farmporject.backend.expert.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.farmporject.backend.expert.repository.ExpertRepository;
import com.farmporject.backend.expert.repository.AnswerRepository;

import com.farmporject.backend.expert.model.*;
import com.farmporject.backend.expert.dto.AnswerDTO;

@Service
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
        Expert expert = expertRepository.findByUser_Id(expert_id).get();
        Question question = qaService.getQuestionById(answerDto.getQuestionId()).get();

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
}
