package com.farmporject.backend.finance.dto;

import java.math.BigDecimal;

/**
 * 联合贷款人/补充资料 DTO
 * 用于接收前端填写的详细信息
 */
public class JointApplicantDTO {

    private Long userId; // 关联的用户ID (如果是补充资料场景)
    private Long loanId; // 关联的贷款ID (如果是补充资料场景)

    private BigDecimal amount;
    private String purpose;
    private String name;
    private String phone;
    private String remark;

    public JointApplicantDTO() {
    }

    public JointApplicantDTO(Long userId, Long loanId, BigDecimal amount, String purpose, String name, String phone,
            String remark) {
        this.userId = userId;
        this.loanId = loanId;
        this.amount = amount;
        this.purpose = purpose;
        this.name = name;
        this.phone = phone;
        this.remark = remark;
    }

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
