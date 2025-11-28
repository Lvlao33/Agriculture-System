package com.farmporject.backend.expert.repository;

import com.farmporject.backend.expert.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 查询用户的所有问题（不排序，在Service层排序）
    @Query("SELECT q FROM Question q WHERE q.userId = :userId")
    List<Question> findByUserId(@Param("userId") String userId);
    
    // 按更新时间降序排列（如果updateTime为null，使用createTime）
    @Query("SELECT q FROM Question q WHERE q.userId = :userId ORDER BY q.updateTime DESC, q.createTime DESC")
    List<Question> findByUserIdOrderByUpdateTimeDesc(@Param("userId") String userId);
    
    // 兼容旧接口
    @Query("SELECT q FROM Question q WHERE q.userId = :userId ORDER BY q.createTime DESC")
    List<Question> findByUserIdOrderByCreateTimeDesc(@Param("userId") String userId);

    List<Question> findByExpertIdOrderByCreateTimeDesc(Long expertId);

    List<Question> findByStatusOrderByCreateTimeDesc(Question.QuestionStatus status);

    List<Question> findByTagsContaining(String tag);

    @Query("SELECT q FROM Question q WHERE q.title LIKE CONCAT('%', :keyword, '%') OR q.content LIKE CONCAT('%', :keyword, '%')")
    List<Question> findByKeyword(@Param("keyword") String keyword);

    List<Question> findByUserIdAndStatusOrderByCreateTimeDesc(String userId, Question.QuestionStatus status);

    long countByExpertIdAndStatus(Long expertId, Question.QuestionStatus status);

    // 查询全部问题（不排序，在Service层排序）
    // 使用默认的findAll()方法
    
    // 兼容旧接口
    List<Question> findTop10ByOrderByCreateTimeDesc();
}