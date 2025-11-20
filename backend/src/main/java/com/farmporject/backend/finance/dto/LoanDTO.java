package com.farmporject.backend.finance.dto;

import java.math.BigDecimal;
import java.util.List;

//创建、修改loan
public class LoanDTO {

    private BigDecimal loanAmount;
    private String loanPurpose;
    private Integer loanTermMonths;
    private BigDecimal interestRate;
    private String remark;  //可为空

    private List<Long> userIds; // 普通用户 ID 数组
    private Long loanProductId; // 贷款产品 ID
    private Long staffId; // 工作人员 ID    //一开始创建可为空
    private Long operatorId; // 操作人员 ID     //可能是用户也有可能是员工

    public LoanDTO() {
    }

    public LoanDTO(BigDecimal loanAmount, String loanPurpose, Integer loanTermMonths, BigDecimal interestRate,
            String remark, List<Long> userIds, Long loanProductId, Long staffId, Long operatorId) {
        this.loanAmount = loanAmount;
        this.loanPurpose = loanPurpose;
        this.loanTermMonths = loanTermMonths;
        this.interestRate = interestRate;
        this.remark = remark;
        this.userIds = userIds;
        this.loanProductId = loanProductId;
        this.staffId = staffId;
        this.operatorId = operatorId;
    }

    // Getters and Setters
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public Long getLoanProductId() {
        return loanProductId;
    }

    public void setLoanProductId(Long loanProductId) {
        this.loanProductId = loanProductId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }   
}
