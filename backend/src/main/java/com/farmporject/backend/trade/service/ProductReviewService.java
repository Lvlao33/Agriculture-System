package com.farmporject.backend.trade.service;

import com.farmporject.backend.trade.model.ProductReview;
import com.farmporject.backend.trade.repository.ProductReviewRepository;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductReviewService(ProductReviewRepository productReviewRepository, UserRepository userRepository) {
        this.productReviewRepository = productReviewRepository;
        this.userRepository = userRepository;
    }

    /**
     * 创建评论
     */
    public ProductReview createReview(Long productId, Long userId, String content, Integer rating) {
        // 验证评分范围
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("评分必须在1-5分之间");
        }

        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        ProductReview review = new ProductReview();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setContent(content);
        review.setRating(rating != null ? rating : 5); // 默认5分
        review.setCreateTime(LocalDateTime.now());
        review.setUserNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        review.setUserAvatar(user.getAvatar());

        return productReviewRepository.save(review);
    }

    /**
     * 根据商品ID获取所有评论
     */
    public List<ProductReview> getReviewsByProductId(Long productId) {
        return productReviewRepository.findByProductIdOrderByCreateTimeDesc(productId);
    }

    /**
     * 根据用户ID获取该用户的所有评论
     */
    public List<ProductReview> getReviewsByUserId(Long userId) {
        return productReviewRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    /**
     * 获取评论详情
     */
    public Optional<ProductReview> getReviewById(Long reviewId) {
        return productReviewRepository.findById(reviewId);
    }

    /**
     * 删除评论
     */
    public void deleteReview(Long reviewId, Long userId) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));

        // 只有评论作者可以删除自己的评论
        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权删除此评论");
        }

        productReviewRepository.delete(review);
    }

    /**
     * 统计商品的评论数量
     */
    public Long getReviewCountByProductId(Long productId) {
        return productReviewRepository.countByProductId(productId);
    }

    /**
     * 计算商品的平均评分
     */
    public Double getAverageRatingByProductId(Long productId) {
        Double avgRating = productReviewRepository.getAverageRatingByProductId(productId);
        return avgRating != null ? avgRating : 0.0;
    }

    /**
     * 检查用户是否已对该商品评论
     */
    public boolean hasUserReviewed(Long productId, Long userId) {
        List<ProductReview> reviews = productReviewRepository.findByProductIdAndUserId(productId, userId);
        return !reviews.isEmpty();
    }
}

