package com.farmporject.backend.expert.repository;

import com.farmporject.backend.expert.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 查询用户的所有问题（不排序，在Service层排序）
    @Query("SELECT q FROM Question q WHERE q.user.id = :userId")
    List<Question> findByUserId(@Param("userId") Long userId);

    // 按更新时间降序排列（如果updateTime为null，使用createTime）
    @Query("SELECT q FROM Question q WHERE q.user.id = :userId ORDER BY q.updateTime DESC, q.createTime DESC")
    List<Question> findByUserIdOrderByUpdateTimeDesc(@Param("userId") Long userId);

    // 兼容旧接口
    @Query("SELECT q FROM Question q WHERE q.user.id = :userId ORDER BY q.createTime DESC")
    List<Question> findByUserIdOrderByCreateTimeDesc(@Param("userId") Long userId);

    @Query("SELECT q FROM Question q WHERE q.expert.id = :expertId ORDER BY q.createTime DESC")
    List<Question> findByExpertIdOrderByCreateTimeDesc(@Param("expertId") Long expertId);

    List<Question> findByStatusOrderByCreateTimeDesc(Question.QuestionStatus status);

    List<Question> findByTagsContaining(String tag);

    @Query("SELECT q FROM Question q WHERE q.title LIKE CONCAT('%', :keyword, '%') OR q.content LIKE CONCAT('%', :keyword, '%')")
    List<Question> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT q FROM Question q WHERE q.user.id = :userId AND q.status = :status ORDER BY q.createTime DESC")
    List<Question> findByUserIdAndStatusOrderByCreateTimeDesc(@Param("userId") Long userId,
            @Param("status") Question.QuestionStatus status);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.expert.id = :expertId AND q.status = :status")
    long countByExpertIdAndStatus(@Param("expertId") Long expertId, @Param("status") Question.QuestionStatus status);

    // 查询全部问题（不排序，在Service层排序）
    // 使用默认的findAll()方法

    // 兼容旧接口
    List<Question> findTop10ByOrderByCreateTimeDesc();

    // 增加浏览量
    @Modifying
    @Query("UPDATE Question q SET q.viewCount = q.viewCount + 1 WHERE q.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // 增加点赞数
    @Modifying
    @Query("UPDATE Question q SET q.likeCount = q.likeCount + 1 WHERE q.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    // 减少点赞数
    @Modifying
    @Query("UPDATE Question q SET q.likeCount = q.likeCount - 1 WHERE q.id = :id AND q.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);
}