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

    /** 更新时间 */
    @Column(name = "update_date")
    private LocalDateTime updateDate;

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

    /** 所关联的处理人员 */
    @Column(name = "handled_by", length = 64)
    private String handledBy;

    public enum LoanStatus {
        REVIEWING, // 银行审核中，提交证明资料银行端未进行操作
        APPROVED, // 审核通过，待签约
        REJECTED, // 审核不通过，用户未修改提交资料/直接不通过，建议更换产品或申请联合贷款
        SIGNED, // 已签约，未放款
        DISBURSED, // 已放款，未确认还款须知？
        REPAYING, // 还款中
        CLEARED_NORMAL, // 正常结清
        CLEARED_EARLY // 提前结清
    }

    public Loan() {
    }

    public Loan(Long farmerId, BigDecimal loanAmount, String loanPurpose, Integer loanTermMonths,
            BigDecimal interestRate, LocalDateTime applicationDate, LocalDateTime updateDate, String status,
            LocalDateTime disbursementDate, LocalDate repaymentDueDate, String remark, Long productId,
            String handledBy) {
        // 用户信息 从登录token中获取
        this.farmerId = farmerId;
        // 用户提交信息
        this.loanAmount = loanAmount; // 前端检测输入的金额是否合理（0~申请额度）
        this.loanPurpose = loanPurpose; // 前端检测输入是否合法（该数据栏位存在长度限制255）

        // 从前端ui信息中获取
        this.productId = productId;

        // 贷款产品信息 先从前端ui获取 后考虑从产品表中获取
        this.loanTermMonths = loanTermMonths;
        this.interestRate = interestRate;

        // 默认值
        this.applicationDate = applicationDate; // 后端获得当前时间 null
        this.updateDate = updateDate; // 后端获得当前时间 null
        this.status = status; // REVIEWING
        this.remark = remark; // null
        this.disbursementDate = disbursementDate; // null
        this.repaymentDueDate = repaymentDueDate; // null

        this.handledBy = handledBy; // null
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

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
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

    public String getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(String handledBy) {
        this.handledBy = handledBy;
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
