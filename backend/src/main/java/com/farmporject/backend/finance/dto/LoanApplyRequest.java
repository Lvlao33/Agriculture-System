package com.farmporject.backend.finance.dto;

import java.math.BigDecimal;

/**
 * 前端提交的贷款申请请求 DTO，包含表单数据和 UI 点击信息
 */
public class LoanApplyRequest {
    private Long farmerId;
    private BigDecimal loanAmount;
    private String loanPurpose;
    private Integer loanTermMonths;
    private BigDecimal interestRate;
    private Long productId;
    private String productName;
    // 来自前端的 UI 点击或上下文，比如按钮 id
    private String clickedButton;

    public LoanApplyRequest() {
    }

    public Long getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public Integer getLoanTermMonths() {
        return loanTermMonths;
    }

    public void setLoanTermMonths(Integer loanTermMonths) {
        this.loanTermMonths = loanTermMonths;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getClickedButton() {
        return clickedButton;
    }

    public void setClickedButton(String clickedButton) {
        this.clickedButton = clickedButton;
    }

    @Override
    public String toString() {
        return "LoanApplyRequest{" +
                "farmerId=" + farmerId +
                ", loanAmount=" + loanAmount +
                ", loanPurpose='" + loanPurpose + '\'' +
                ", loanTermMonths=" + loanTermMonths +
                ", interestRate=" + interestRate +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", clickedButton='" + clickedButton + '\'' +
                '}';
    }
}