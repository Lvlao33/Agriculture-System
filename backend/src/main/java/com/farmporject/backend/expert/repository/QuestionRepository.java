package com.farmporject.backend.expert.repository;

import com.farmporject.backend.expert.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByUserIdOrderByCreateTimeDesc(String userId);

    List<Question> findByExpertIdOrderByCreateTimeDesc(Long expertId);

    List<Question> findByStatusOrderByCreateTimeDesc(Question.QuestionStatus status);

    List<Question> findByTagsContaining(String tag);

    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword%")
    List<Question> findByKeyword(String keyword);

    List<Question> findByUserIdAndStatusOrderByCreateTimeDesc(String userId, Question.QuestionStatus status);

    long countByExpertIdAndStatus(Long expertId, Question.QuestionStatus status);

    List<Question> findTop10ByOrderByCreateTimeDesc();
}