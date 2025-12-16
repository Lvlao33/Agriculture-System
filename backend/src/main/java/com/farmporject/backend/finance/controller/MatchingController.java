package com.farmporject.backend.finance.controller;

import com.farmporject.backend.finance.dto.LoanIntentionDTO;
import com.farmporject.backend.finance.dto.LoanMatchResultDTO;
import com.farmporject.backend.finance.service.LoanIntentionService;
import jakarta.servlet.http.HttpServletRequest;
import com.farmporject.backend.common.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 融资-智能匹配控制器
 * 提供融资意向管理和智能匹配功能
 */
@RestController
@RequestMapping("/api/finance/matching")
public class MatchingController {

    @Autowired
    private LoanIntentionService loanIntentionService;

    /**
     * 创建或更新融资意向
     * POST /api/finance/matching/intention
     */
    @PostMapping("/intention")
    public ApiResponse<?> saveIntention(@RequestBody LoanIntentionDTO intentionDTO,
            HttpServletRequest request) {
        try {
            // 从token中获取用户ID
            String token = request.getHeader("Authorization");
            Long userId = getUserIdFromToken(token);

            if (userId == null) {
                return ApiResponse.fail("未授权访问");
            }

            // 保存融资意向
            LoanIntentionDTO saved = loanIntentionService.saveIntention(userId, intentionDTO);

            return ApiResponse.success(saved);
        } catch (Exception e) {
            return ApiResponse.fail("保存失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的融资意向
     * GET /api/finance/matching/intention
     */
    @GetMapping("/intention")
    public ApiResponse<?> getIntention(HttpServletRequest request) {
        try {
            // 从token中获取用户ID
            String token = request.getHeader("Authorization");
            Long userId = getUserIdFromToken(token);

            if (userId == null) {
                return ApiResponse.fail("未授权访问");
            }

            Optional<LoanIntentionDTO> intention = loanIntentionService.getIntentionByUserId(userId);

            if (intention.isPresent()) {
                return ApiResponse.success(intention.get());
            } else {
                // 返回空数据而不是错误
                return ApiResponse.success(null);
            }
        } catch (Exception e) {
            return ApiResponse.fail("查询失败: " + e.getMessage());
        }
    }

    /**
     * 删除融资意向
     * DELETE /api/finance/matching/intention
     */
    @DeleteMapping("/intention")
    public ApiResponse<?> deleteIntention(HttpServletRequest request) {
        try {
            // 从token中获取用户ID
            String token = request.getHeader("Authorization");
            Long userId = getUserIdFromToken(token);

            if (userId == null) {
                return ApiResponse.fail("未授权访问");
            }

            loanIntentionService.deleteIntention(userId);

            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.fail("删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取推荐的联合贷款人列表
     * GET /api/finance/matching/recommend
     */
    @GetMapping("/recommend")
    public ApiResponse<?> getRecommendations(HttpServletRequest request) {
        try {
            // 从token中获取用户ID
            String token = request.getHeader("Authorization");
            Long userId = getUserIdFromToken(token);

            if (userId == null) {
                return ApiResponse.fail("未授权访问");
            }

            List<LoanMatchResultDTO> recommendations = loanIntentionService.getRecommendedUsers(userId);

            return ApiResponse.success(recommendations);
        } catch (Exception e) {
            return ApiResponse.fail("获取推荐失败: " + e.getMessage());
        }
    }

    /**
     * 从token中提取用户ID
     * token格式: "tk_id_username"
     */
    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("tk_")) {
            return null;
        }
        try {
            String[] parts = token.split("_");
            if (parts.length >= 2) {
                return Long.parseLong(parts[1]);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }
}
