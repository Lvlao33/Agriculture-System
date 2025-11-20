package com.farmporject.backend.expert.repository;

import com.farmporject.backend.expert.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestionIdOrderByCreateTime(Long questionId);

    List<Answer> findByExpertIdOrderByCreateTimeDesc(Long expertId);

    List<Answer> findByQuestionIdAndExpertId(Long questionId, Long expertId);

    @Query("SELECT a FROM Answer a WHERE a.expert.id = :expertId AND a.question.status = 'ANSWERED'")
    List<Answer> findAnsweredQuestionsByExpertId(Long expertId);

    long countByExpertId(Long expertId);

    List<Answer> findByQuestionIdIn(List<Long> questionIds);

    @Query("SELECT a FROM Answer a WHERE a.content LIKE %:keyword%")
    List<Answer> findByContentContaining(String keyword);
}