package com.farmporject.backend.finance.model;

import jakarta.persistence.*;
import com.farmporject.backend.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class LoanUserStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_user_status_id", nullable = false)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "loan_id")
    private Loan loan; // 关联贷款

    /**
     * 该申请人/用户在此笔贷款中的具体信息
     * 主贷人在创建贷款时写入，联合贷人在补充资料时写入
     */
    @Column(name = "amount", precision = 14, scale = 2)
    private java.math.BigDecimal amount; // 申请/分担金额

    @Column(name = "purpose", length = 255)
    private String purpose; // 贷款用途（个人视角）

    @Column(name = "name", length = 64)
    private String name; // 真实姓名

    @Column(name = "phone", length = 20)
    private String phone; // 联系电话

    @Column(name = "remark", length = 1000)
    private String remark; // 备注/描述

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 关联农户

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status; // 贷款的状态（例如：待审核，已批准等）

    public LoanUserStatus() {
    }

    public LoanUserStatus(Loan loan, User user, Status status) {
        this.loan = loan;
        this.user = user;
        this.status = status;
    }

    // Getters and Setters
    public java.math.BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(java.math.BigDecimal amount) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LoanUserStatus{" +
                "id=" + id +
                ", loan=" + loan +
                ", user=" + user +
                ", status=" + status +
                '}';
    }
}
