package com.farmporject.backend.finance.dto;

import java.io.Serializable;

/**
 * 智能匹配结果 DTO
 * 包含匹配到的用户、匹配分数以及可能的详细信息
 */
public class LoanMatchResultDTO implements Serializable {
    private LoanIntentionDTO intention;
    private Integer score;
    private String matchReason; // 可选：匹配原因摘要

    public LoanMatchResultDTO() {
    }

    public LoanMatchResultDTO(LoanIntentionDTO intention, Integer score) {
        this.intention = intention;
        this.score = score;
    }

    public LoanIntentionDTO getIntention() {
        return intention;
    }

    public void setIntention(LoanIntentionDTO intention) {
        this.intention = intention;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }
}
