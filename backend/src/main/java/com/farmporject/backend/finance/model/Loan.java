package com.farmporject.backend.finance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 贷款实体（Loan）的数据模型，映射到数据库表 `loans`。
 */
@Entity
@Table(name = "loans")
public class Loan implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键，自增ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联的农户ID（外键） */
    @Column(name = "farmer_id", nullable = false)
    private Long farmerId;

    /** 申请金额 */
    @Column(name = "loan_amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal loanAmount;

    /** 贷款用途 */
    @Column(name = "loan_purpose", length = 255)
    private String loanPurpose;

    /** 贷款期限（月） */
    @Column(name = "loan_term_months")
    private Integer loanTermMonths;

    /** 利率（年化） */
    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    /** 申请时间 */
    @Column(name = "application_date")
    private LocalDateTime applicationDate;

    /** 当前状态（例如：PENDING/APPROVED/REJECTED） */
    @Column(name = "status", length = 32)
    private String status;

    /** 放款时间 */
    @Column(name = "disbursement_date")
    private LocalDateTime disbursementDate;

    /** 还款截止日期 */
    @Column(name = "repayment_due_date")
    private LocalDate repaymentDueDate;

    /** 备注 */
    @Column(name = "remark", length = 1000)
    private String remark;

    /** 所关联的产品ID */
    @Column(name = "product_id")
    private Long productId;

    public enum LoanStatus {
        APPLIED, // 客户已申请
        REVIEWING, // 银行审核中
        APPROVED, // 审核通过，待签约
        REJECTED, // 审核不通过
        SIGNED, // 已签约
        DISBURSED, // 已放款
        REPAYING, // 还款中
        CLEARED_NORMAL, // 正常结清
        CLEARED_EARLY // 提前结清
    }

    public Loan() {
    }

    public Loan(Long farmerId, BigDecimal loanAmount, String loanPurpose, Integer loanTermMonths,
            BigDecimal interestRate, LocalDateTime applicationDate, String status,
            LocalDateTime disbursementDate, LocalDate repaymentDueDate, String remark,
            Long productId) {
        // 用户自填字段
        this.loanAmount = loanAmount;
        this.loanPurpose = loanPurpose;
        this.productId = productId;

        // 系统自动生成或管理员填写字段
        this.farmerId = farmerId;
        this.loanTermMonths = loanTermMonths;
        this.interestRate = interestRate;
        this.status = status;
        this.applicationDate = applicationDate;
        this.disbursementDate = disbursementDate;
        this.repaymentDueDate = repaymentDueDate;
        this.remark = null;
    }

    // Getter / Setter
    // getter和setter方法，用于从数据库中读取数据并设置到实体类中,但与数据库操作不同
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(LocalDateTime disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public LocalDate getRepaymentDueDate() {
        return repaymentDueDate;
    }

    public void setRepaymentDueDate(LocalDate repaymentDueDate) {
        this.repaymentDueDate = repaymentDueDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", farmerId=" + farmerId +
                ", loanAmount=" + loanAmount +
                ", loanPurpose='" + loanPurpose + '\'' +
                ", loanTermMonths=" + loanTermMonths +
                ", interestRate=" + interestRate +
                ", applicationDate=" + applicationDate +
                ", status='" + status + '\'' +
                ", disbursementDate=" + disbursementDate +
                ", repaymentDueDate=" + repaymentDueDate +
                ", remark='" + remark + '\'' +
                ", productId=" + productId +
                "}";
    }
}
