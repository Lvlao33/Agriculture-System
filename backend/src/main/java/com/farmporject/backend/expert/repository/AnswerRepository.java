package com.farmporject.backend.expert.repository;

import com.farmporject.backend.expert.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Answer> findByExpertIdAndQuestionId(Long expertId, Long questionId);

    List<Answer> findByQuestionId(Long questionId);

    // 增加浏览量
    @Modifying
    @Query("UPDATE Answer a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // 增加点赞数
    @Modifying
    @Query("UPDATE Answer a SET a.likeCount = a.likeCount + 1 WHERE a.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    // 减少点赞数
    @Modifying
    @Query("UPDATE Answer a SET a.likeCount = a.likeCount - 1 WHERE a.id = :id AND a.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);
}