package com.farmporject.backend.finance.service;

import com.farmporject.backend.finance.dto.LoanIntentionDTO;
import com.farmporject.backend.finance.model.LoanIntention;
import com.farmporject.backend.finance.repository.LoanIntentionRepository;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 融资意向服务
 */
@Service
public class LoanIntentionService {

    @Autowired
    private LoanIntentionRepository loanIntentionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建或更新融资意向
     */
    @Transactional
    public LoanIntentionDTO saveIntention(Long userId, LoanIntentionDTO dto) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 查找是否已存在融资意向
        Optional<LoanIntention> existingIntention = loanIntentionRepository.findByUserId(userId);

        LoanIntention intention;
        if (existingIntention.isPresent()) {
            // 更新现有意向
            intention = existingIntention.get();
        } else {
            // 创建新意向
            intention = new LoanIntention();
            intention.setUser(user);
        }

        // 设置字段
        intention.setRealName(dto.getRealName());
        intention.setAmount(dto.getAmount());
        intention.setPhone(dto.getPhone());
        intention.setApplication(dto.getApplication());
        intention.setAnnualIncome(dto.getAnnualIncome());
        intention.setRepaymentPeriod(dto.getRepaymentPeriod());
        intention.setOtherNeeds(dto.getOtherNeeds());

        LoanIntention saved = loanIntentionRepository.save(intention);
        return convertToDTO(saved);
    }

    /**
     * 获取当前用户的融资意向
     */
    public Optional<LoanIntentionDTO> getIntentionByUserId(Long userId) {
        return loanIntentionRepository.findByUserId(userId)
                .map(this::convertToDTO);
    }

    /**
     * 删除融资意向
     */
    @Transactional
    public void deleteIntention(Long userId) {
        loanIntentionRepository.deleteByUserId(userId);
    }

    /**
     * 获取推荐的联合贷款人列表
     * 基于智能匹配算法
     */
    public List<com.farmporject.backend.finance.dto.LoanMatchResultDTO> getRecommendedUsers(Long userId) {
        // 获取当前用户的融资意向
        Optional<LoanIntention> currentIntention = loanIntentionRepository.findByUserId(userId);
        if (currentIntention.isEmpty()) {
            return new ArrayList<>();
        }

        LoanIntention current = currentIntention.get();

        // 获取其他用户的融资意向
        List<LoanIntention> otherIntentions = loanIntentionRepository.findAllByUserIdNot(userId);

        // 计算匹配度并排序
        return otherIntentions.stream()
                .map(intention -> {
                    double matchScore = calculateMatchScore(current, intention);
                    // 转换为结果DTO
                    com.farmporject.backend.finance.dto.LoanMatchResultDTO resultDTO = new com.farmporject.backend.finance.dto.LoanMatchResultDTO();
                    resultDTO.setIntention(convertToDTO(intention));
                    resultDTO.setScore((int) matchScore);
                    // 可以设置匹配原因摘要
                    resultDTO.setMatchReason(generateMatchReason(matchScore));
                    return resultDTO;
                })
                .filter(dto -> dto.getScore() >= 20) // 只返回分数大于等于20的用户
                .sorted(Comparator.comparingInt(com.farmporject.backend.finance.dto.LoanMatchResultDTO::getScore)
                        .reversed()) // 按分数降序
                .limit(10) // 返回前10个最匹配的用户
                .collect(Collectors.toList());
    }

    private String generateMatchReason(double score) {
        if (score >= 90)
            return "极高匹配度，建议立即联系";
        if (score >= 80)
            return "高匹配度，资金用途高度一致";
        if (score >= 70)
            return "中等匹配度，值得尝试";
        return "一般匹配度";
    }

    /**
     * 计算两个融资意向的匹配度
     * 返回值范围：0-100
     */
    private double calculateMatchScore(LoanIntention current, LoanIntention other) {
        double score = 0.0;

        // 1. 金额匹配度（权重：35%）
        double amountScore = calculateAmountMatchScore(current.getAmount(), other.getAmount());
        score += amountScore * 0.35;

        // 2. 年收入匹配度（权重：30%）
        double incomeScore = calculateAmountMatchScore(current.getAnnualIncome(), other.getAnnualIncome());
        score += incomeScore * 0.30;

        // 3. 借款期限匹配度（权重：20%）
        double periodScore = calculatePeriodMatchScore(current.getRepaymentPeriod(), other.getRepaymentPeriod());
        score += periodScore * 0.20;

        // 4. 用途匹配度（权重：15%）
        double appScore = calculateStringMatchScore(current.getApplication(), other.getApplication());
        score += appScore * 0.15;

        return score;
    }

    /**
     * 计算金额/收入匹配度
     */
    private double calculateAmountMatchScore(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null || amount2 == null) {
            return 0.0;
        }

        BigDecimal diff = amount1.subtract(amount2).abs();
        BigDecimal avg = amount1.add(amount2).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);

        if (avg.compareTo(BigDecimal.ZERO) == 0) {
            return 100.0;
        }

        // 差异百分比
        double diffPercent = diff.divide(avg, 4, RoundingMode.HALF_UP).doubleValue();

        // 差异越小，分数越高
        // 差异在10%以内得满分，差异超过50%得0分
        if (diffPercent <= 0.1) {
            return 100.0;
        } else if (diffPercent >= 0.5) {
            return 0.0;
        } else {
            return 100.0 - (diffPercent - 0.1) / 0.4 * 100.0;
        }
    }

    /**
     * 计算借款期限匹配度
     */
    private double calculatePeriodMatchScore(Integer period1, Integer period2) {
        if (period1 == null || period2 == null) {
            return 0.0;
        }

        // 完全相同得满分
        if (period1.equals(period2)) {
            return 100.0;
        }

        int diff = Math.abs(period1 - period2);

        // 差异在6个月以内得高分，差异超过24个月得0分
        if (diff <= 6) {
            return 100.0 - diff * 10.0;
        } else if (diff >= 24) {
            return 0.0;
        } else {
            return 40.0 - (diff - 6) / 18.0 * 40.0;
        }
    }

    /**
     * 计算字符串匹配度（用于用途）
     * 使用简单的字符串相似度算法
     */
    private double calculateStringMatchScore(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0.0;
        }

        str1 = str1.trim().toLowerCase();
        str2 = str2.trim().toLowerCase();

        // 完全相同得满分
        if (str1.equals(str2)) {
            return 100.0;
        }

        // 包含关系得高分
        if (str1.contains(str2) || str2.contains(str1)) {
            return 80.0;
        }

        // 使用Levenshtein距离计算相似度
        int distance = levenshteinDistance(str1, str2);
        int maxLen = Math.max(str1.length(), str2.length());

        if (maxLen == 0) {
            return 100.0;
        }

        double similarity = 1.0 - (double) distance / maxLen;
        return similarity * 100.0;
    }

    /**
     * 计算Levenshtein距离（编辑距离）
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * 将实体转换为DTO
     */
    private LoanIntentionDTO convertToDTO(LoanIntention intention) {
        LoanIntentionDTO dto = new LoanIntentionDTO();
        dto.setId(intention.getId());
        dto.setUserId(intention.getUser().getId());
        dto.setUserName(intention.getUser().getUsername());
        dto.setRealName(intention.getRealName());
        dto.setAmount(intention.getAmount());
        dto.setPhone(intention.getPhone());
        dto.setApplication(intention.getApplication());
        dto.setAnnualIncome(intention.getAnnualIncome());
        dto.setRepaymentPeriod(intention.getRepaymentPeriod());
        dto.setOtherNeeds(intention.getOtherNeeds());
        dto.setAvatar(intention.getUser().getAvatar());
        return dto;
    }
}
