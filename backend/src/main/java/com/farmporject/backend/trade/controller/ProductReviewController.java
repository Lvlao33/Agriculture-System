package com.farmporject.backend.trade.controller;

import com.farmporject.backend.config.JwtConfig;
import com.farmporject.backend.trade.model.ProductReview;
import com.farmporject.backend.trade.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品评论控制器
 * 路由前缀：/api/trade/products/{productId}/reviews
 * - GET /api/trade/products/{productId}/reviews 获取商品评论列表
 * - POST /api/trade/products/{productId}/reviews 创建评论
 * - DELETE /api/trade/products/{productId}/reviews/{reviewId} 删除评论
 */
@RestController
@RequestMapping("/api/trade/products/{productId}/reviews")
public class ProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 获取商品的所有评论
     */
    @GetMapping
    public ResponseEntity<?> getReviews(@PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ProductReview> reviews = productReviewService.getReviewsByProductId(productId);
            Long reviewCount = productReviewService.getReviewCountByProductId(productId);
            Double avgRating = productReviewService.getAverageRatingByProductId(productId);

            Map<String, Object> data = new HashMap<>();
            data.put("list", reviews);
            data.put("total", reviewCount);
            data.put("averageRating", avgRating);

            response.put("flag", true);
            response.put("data", data);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取评论列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 创建评论
     * 从Authorization header中解析userId（支持JWT和旧格式token）
     */
    @PostMapping
    public ResponseEntity<?> createReview(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 解析userId（支持JWT和旧格式token）
            Long userId = null;
            if (token != null) {
                // 移除Bearer前缀（如果存在）
                String actualToken = token;
                if (token.startsWith("Bearer ")) {
                    actualToken = token.substring(7);
                }
                
                // 优先尝试解析 JWT token
                try {
                    if (jwtConfig.validateToken(actualToken)) {
                        userId = jwtConfig.getUserIdFromToken(actualToken);
                        if (userId != null) {
                            // JWT解析成功，直接使用
                        }
                    }
                } catch (Exception e) {
                    // JWT解析失败，继续尝试旧格式
                }
                
                // 如果JWT解析失败，尝试旧格式（tk_xxx）
                if (userId == null && actualToken.startsWith("tk_")) {
                    String[] parts = actualToken.split("_");
                    if (parts.length >= 2) {
                        try {
                            userId = Long.parseLong(parts[1]);
                        } catch (NumberFormatException ex) {
                            // 忽略解析错误
                        }
                    }
                }
            }
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查是否已评论
            if (productReviewService.hasUserReviewed(productId, userId)) {
                response.put("flag", false);
                response.put("message", "您已经评论过该商品");
                return ResponseEntity.badRequest().body(response);
            }

            // 获取评论内容
            String content = (String) requestBody.get("content");
            if (content == null || content.trim().isEmpty()) {
                response.put("flag", false);
                response.put("message", "评论内容不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 获取评分（可选）
            Integer rating = null;
            Object ratingObj = requestBody.get("rating");
            if (ratingObj instanceof Number) {
                rating = ((Number) ratingObj).intValue();
            } else if (ratingObj instanceof String) {
                try {
                    rating = Integer.parseInt((String) ratingObj);
                } catch (NumberFormatException e) {
                    // 忽略解析错误，使用默认值
                }
            }

            ProductReview review = productReviewService.createReview(productId, userId, content, rating);

            response.put("flag", true);
            response.put("message", "评论成功");
            response.put("data", review);
            return ResponseEntity.status(201).body(response);
        } catch (IllegalArgumentException e) {
            response.put("flag", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "创建评论失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 解析userId（支持JWT和旧格式token）
            Long userId = null;
            if (token != null) {
                // 移除Bearer前缀（如果存在）
                String actualToken = token;
                if (token.startsWith("Bearer ")) {
                    actualToken = token.substring(7);
                }
                
                // 尝试使用JWT解析
                try {
                    userId = jwtConfig.getUserIdFromToken(actualToken);
                } catch (Exception e) {
                    // 如果JWT解析失败，尝试旧格式（tk_xxx）
                    if (actualToken.startsWith("tk_")) {
                        String[] parts = actualToken.split("_");
                        if (parts.length >= 2) {
                            try {
                                userId = Long.parseLong(parts[1]);
                            } catch (NumberFormatException ex) {
                                // 忽略解析错误
                            }
                        }
                    }
                }
            }
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.badRequest().body(response);
            }

            productReviewService.deleteReview(reviewId, userId);

            response.put("flag", true);
            response.put("message", "删除评论成功");
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            response.put("flag", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "删除评论失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

