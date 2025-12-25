package com.farmporject.backend.trade.repository;

import com.farmporject.backend.trade.model.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    /**
     * 根据商品ID查询所有评论，按创建时间倒序
     */
    List<ProductReview> findByProductIdOrderByCreateTimeDesc(Long productId);

    /**
     * 根据用户ID查询该用户的所有评论
     */
    List<ProductReview> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 根据商品ID和用户ID查询评论（用于判断用户是否已评论）
     */
    List<ProductReview> findByProductIdAndUserId(Long productId, Long userId);

    /**
     * 统计商品的评论数量
     */
    @Query("SELECT COUNT(r) FROM ProductReview r WHERE r.productId = :productId")
    Long countByProductId(@Param("productId") Long productId);

    /**
     * 计算商品的平均评分
     */
    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.productId = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
}

