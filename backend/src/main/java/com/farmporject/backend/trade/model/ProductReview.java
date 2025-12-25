package com.farmporject.backend.trade.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商品评论实体类
 * 用于存储买家对商品的评论
 */
@Entity
@Table(name = "product_reviews")
public class ProductReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 2000)
    private String content;

    @Column(nullable = false)
    private Integer rating = 5; // 评分，1-5分，默认5分

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    // 关联用户昵称（冗余字段，便于查询显示）
    @Column(length = 64)
    private String userNickname;

    // 关联用户头像（冗余字段，便于查询显示）
    @Column(length = 256)
    private String userAvatar;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
}

