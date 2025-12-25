package com.farmporject.backend.finance.dto;

import java.math.BigDecimal;
import java.util.List;

//创建、修改loan
public class LoanDTO {

    private BigDecimal loanAmount;
    private String loanPurpose;
    private Integer loanTermMonths;
    private BigDecimal interestRate;
    private String remark; // 可为空

    private String applicantName;
    private String applicantPhone;

    private List<JointApplicantDTO> jointApplicants; // 联合贷款人（包括ID和其他信息），如果是创建，则只需要ID即可，其他信息为空
    // 为了兼容旧代码，这里其实应该保留 userIds 或者在 Service 层做转换，
    // 但根据需求“整体变动最小”，我们假设前端会改为传对象列表，或者我们暂时兼容处理。
    // 鉴于 LoanService 的 apply 方法目前使用 userIds，
    // 最佳策略是：LoanDTO 增加 jointApplicants 字段，userIds 字段保留用于向后兼容或简化传参。
    private List<Long> userIds; // 保留此字段以兼容现有逻辑，或者作为主申请人之外的简单ID列表
    private Long loanProductId; // 贷款产品 ID
    private Long staffId; // 工作人员 ID //一开始创建可为空
    private Long operatorId; // 操作人员 ID //可能是用户也有可能是员工

    private Long id; // 贷款 ID //可能为空

    public LoanDTO() {
    }

    public LoanDTO(BigDecimal loanAmount, String loanPurpose, Integer loanTermMonths, BigDecimal interestRate,
            String remark, List<Long> userIds, Long loanProductId, Long staffId, Long operatorId, Long id) {
        this.loanAmount = loanAmount;
        this.loanPurpose = loanPurpose;
        this.loanTermMonths = loanTermMonths;
        this.interestRate = interestRate;
        this.remark = remark;
        this.userIds = userIds;
        this.loanProductId = loanProductId;
        this.staffId = staffId;
        this.operatorId = operatorId;
        this.id = id;
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

    public List<JointApplicantDTO> getJointApplicants() {
        return jointApplicants;
    }

    public void setJointApplicants(List<JointApplicantDTO> jointApplicants) {
        this.jointApplicants = jointApplicants;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }
}
