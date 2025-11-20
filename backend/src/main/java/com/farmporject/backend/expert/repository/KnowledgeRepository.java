package com.farmporject.backend.expert.repository;

import com.farmporject.backend.expert.model.Knowledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {

    // 分页查询已发布的知识
    Page<Knowledge> findByIsPublishedTrue(Pageable pageable);

    // 根据分类查询
    List<Knowledge> findByCategoriesContainingAndIsPublishedTrue(String category);

    // 根据标签查询
    List<Knowledge> findByTagsContainingAndIsPublishedTrue(String tag);

    // 关键词搜索（标题、内容、摘要）
    @Query("SELECT k FROM Knowledge k WHERE k.isPublished = true AND " +
            "(k.title LIKE %:keyword% OR k.content LIKE %:keyword% OR k.summary LIKE %:keyword%)")
    List<Knowledge> findByKeyword(@Param("keyword") String keyword);

    // 分页关键词搜索
    @Query("SELECT k FROM Knowledge k WHERE k.isPublished = true AND " +
            "(k.title LIKE %:keyword% OR k.content LIKE %:keyword% OR k.summary LIKE %:keyword%)")
    Page<Knowledge> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 根据作者查询
    List<Knowledge> findByAuthorAndIsPublishedTrue(String author);

    // 获取热门知识（按浏览量排序）
    List<Knowledge> findTop10ByIsPublishedTrueOrderByViewCountDesc();

    // 获取最新知识
    List<Knowledge> findTop10ByIsPublishedTrueOrderByCreateTimeDesc();

    // 根据发布状态查询
    Page<Knowledge> findByIsPublished(Boolean isPublished, Pageable pageable);

    // 增加浏览量
    @Modifying
    @Query("UPDATE Knowledge k SET k.viewCount = k.viewCount + 1 WHERE k.knowledgeId = :knowledgeId")
    void incrementViewCount(@Param("knowledgeId") Long knowledgeId);

    // 增加点赞数
    @Modifying
    @Query("UPDATE Knowledge k SET k.likeCount = k.likeCount + 1 WHERE k.knowledgeId = :knowledgeId")
    void incrementLikeCount(@Param("knowledgeId") Long knowledgeId);

    // 减少点赞数
    @Modifying
    @Query("UPDATE Knowledge k SET k.likeCount = k.likeCount - 1 WHERE k.knowledgeId = :knowledgeId AND k.likeCount > 0")
    void decrementLikeCount(@Param("knowledgeId") Long knowledgeId);

    // 获取所有分类（去重）
    @Query("SELECT DISTINCT c FROM Knowledge k JOIN k.categories c WHERE k.isPublished = true")
    List<String> findAllCategories();

    // 获取所有标签（去重）
    @Query("SELECT DISTINCT t FROM Knowledge k JOIN k.tags t WHERE k.isPublished = true")
    List<String> findAllTags();

    // 统计分类下的知识数量
    @Query("SELECT COUNT(k) FROM Knowledge k WHERE :category MEMBER OF k.categories AND k.isPublished = true")
    long countByCategory(@Param("category") String category);

    // 统计标签下的知识数量
    @Query("SELECT COUNT(k) FROM Knowledge k WHERE :tag MEMBER OF k.tags AND k.isPublished = true")
    long countByTag(@Param("tag") String tag);
}