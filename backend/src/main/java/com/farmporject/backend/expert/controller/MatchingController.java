package com.farmporject.backend.finance.controller;

import com.farmporject.backend.finance.dto.LoanIntentionDTO;
import com.farmporject.backend.finance.dto.LoanMatchResultDTO;
import com.farmporject.backend.finance.dto.ComboRecommendationDTO;
import com.farmporject.backend.finance.service.LoanIntentionService;
import com.farmporject.backend.security.UserContext;
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
    public ApiResponse<?> saveIntention(@RequestBody LoanIntentionDTO intentionDTO) {
        try {
            // 从UserContext获取用户ID
            Long userId = UserContext.getCurrentUserId();

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
    public ApiResponse<?> getIntention() {
        try {
            // 从UserContext获取用户ID
            Long userId = UserContext.getCurrentUserId();

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
    public ApiResponse<?> deleteIntention() {
        try {
            // 从UserContext获取用户ID
            Long userId = UserContext.getCurrentUserId();

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
    public ApiResponse<?> getRecommendations() {
        try {
            // 从UserContext获取用户ID
            Long userId = UserContext.getCurrentUserId();

            if (userId == null) {
                return ApiResponse.fail("未授权访问");
            }

            List<LoanMatchResultDTO> recommendations = loanIntentionService.getRecommendedUsers(userId);

            return ApiResponse.success(recommendations);
        } catch (Exception e) {
            return ApiResponse.fail("获取推荐失败: " + e.getMessage());
        }
    }

    @Autowired
    private com.farmporject.backend.finance.service.LoanProductService loanProductService;

    /**
     * 获取联合贷款推荐产品
     * GET /api/finance/matching/recommend-combo
     */
    @GetMapping("/recommend-combo")
    public ApiResponse<?> getComboRecommendations(@RequestParam Long partnerId) {
        try {
            // 从UserContext获取用户ID
            Long userId = UserContext.getCurrentUserId();

            if (userId == null) {
                return ApiResponse.fail("未授权访问");
            }

            ComboRecommendationDTO recommendation = loanProductService.recommendCombo(userId, partnerId);

            return ApiResponse.success(recommendation);
        } catch (Exception e) {
            return ApiResponse.fail("获取推荐失败: " + e.getMessage());
        }
    }

}
