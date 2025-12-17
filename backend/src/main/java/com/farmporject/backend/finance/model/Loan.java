package com.farmporject.backend.finance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import java.util.List;
import java.util.ArrayList;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.farmporject.backend.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @Column(name = "loan_id")
    private Long id;

    /** 申请金额 */
    @Column(name = "loan_amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal loanAmount;

    /** 贷款用途 */
    @Column(name = "loan_purpose", length = 255)
    private String loanPurpose;

    /** 申请人真实姓名 */
    @Column(name = "applicant_name", length = 64)
    private String applicantName;

    /** 申请人联系电话 */
    @Column(name = "applicant_phone", length = 20)
    private String applicantPhone;

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
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** 放款时间 */
    @Column(name = "disbursement_date")
    private LocalDateTime disbursementDate;

    /** 还款截止日期 */
    @Column(name = "repayment_due_date")
    private LocalDate repaymentDueDate;

    /** 备注 */
    @Column(name = "remark", length = 1000)
    private String remark;

    // 表与表的关系
    /** 关联的农户ID（外键） */
    @OneToMany(mappedBy = "loan")
    @JsonIgnore
    private List<LoanUserStatus> loanUserStatuses; // 贷款与多个农户的多对多关系

    /** 所关联的产品ID */
    @ManyToOne
    @JoinColumn(name = "loan_product_id")
    @JsonIgnore
    private LoanProduct loanProduct; // 贷款产品与 Loan 的一对一关系

    /** 所关联的资料文件ID */
    @OneToMany(mappedBy = "loan")
    @JsonIgnore
    private List<LoanFile> loanFiles; // 资料文件与 Loan 的多对一关系

    /** 所关联的处理人员 */
    @ManyToOne
    @JoinColumn(name = "staff_id")
    @JsonIgnore
    private User staff; // 工作人员与 Loan 的一对多关系

    /** 关联的贷款记录ID */
    @OneToMany(mappedBy = "loan")
    @JsonIgnore
    private List<LoanRecord> loanRecords; // 贷款记录与 Loan 的多对一关系

    public Loan() {
        loanUserStatuses = new ArrayList<>();
        loanFiles = new ArrayList<>();
        loanRecords = new ArrayList<>();
    }

    // 现在使用DTO模式，不再使用该构造方法
    // 初始化
    public Loan(BigDecimal loanAmount, String loanPurpose, Integer loanTermMonths,
            BigDecimal interestRate, LocalDateTime applicationDate, LocalDateTime updateDate, Status status,
            LocalDateTime disbursementDate, LocalDate repaymentDueDate, String remark) {
        // 用户提交信息
        this.loanAmount = loanAmount; // 前端检测输入的金额是否合理（0~申请额度）
        this.loanPurpose = loanPurpose; // 前端检测输入是否合法（该数据栏位存在长度限制255）

        // 贷款产品信息
        this.loanTermMonths = loanTermMonths;
        this.interestRate = interestRate;

        // 默认值
        this.applicationDate = applicationDate; // 后端获得当前时间
        this.updateDate = updateDate; // 后端获得当前时间
        this.status = status; // CREATED
        this.remark = remark; //
        this.disbursementDate = disbursementDate; // null
        this.repaymentDueDate = repaymentDueDate; // null
    }

    // Getter / Setter
    // getter和setter方法，用于从数据库中读取数据并设置到实体类中,但与数据库操作不同
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    // 表与表的关系

    public LoanProduct getLoanProduct() {
        return loanProduct;
    }

    public void setLoanProduct(LoanProduct loanProduct) {
        this.loanProduct = loanProduct;
    }

    public List<LoanFile> getLoanFiles() {
        return loanFiles;
    }

    public void setLoanFiles(List<LoanFile> loanFiles) {
        this.loanFiles = loanFiles;
    }

    public User getStaff() {
        return staff;
    }

    public void setStaff(User staff) {
        this.staff = staff;
    }

    public List<LoanRecord> getLoanRecords() {
        return loanRecords;
    }

    public void setLoanRecords(List<LoanRecord> loanRecords) {
        this.loanRecords = loanRecords;
    }

    public List<LoanUserStatus> getLoanUserStatuses() {
        return loanUserStatuses;
    }

    public void setLoanUserStatuses(List<LoanUserStatus> loanUserStatuses) {
        this.loanUserStatuses = loanUserStatuses;
    }

    @Override
    public String toString() {
        return "Loan [id=" + id + ", loanAmount=" + loanAmount + ", loanPurpose=" + loanPurpose + ", loanTermMonths="
                + loanTermMonths + ", interestRate=" + interestRate + ", applicationDate=" + applicationDate
                + ", updateDate=" + updateDate + ", status=" + status + ", disbursementDate=" + disbursementDate
                + ", repaymentDueDate=" + repaymentDueDate + ", remark=" + remark + "]";
    }
}
