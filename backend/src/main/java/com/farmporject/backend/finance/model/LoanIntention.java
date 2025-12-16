package com.farmporject.backend.finance.model;

import com.farmporject.backend.user.model.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 融资意向实体
 * 存储用户的联合贷款意向信息
 */
@Entity
@Table(name = "loan_intention")
public class LoanIntention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "real_name", nullable = false, length = 64)
    private String realName;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "application", nullable = false, length = 255)
    private String application;

    @Column(name = "annual_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal annualIncome; // 年收入

    @Column(name = "repayment_period", nullable = false)
    private Integer repaymentPeriod; // 意向借款期（月）

    @Column(name = "other_needs", length = 500)
    private String otherNeeds; // 其他需求

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public LoanIntention() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }

    public Integer getRepaymentPeriod() {
        return repaymentPeriod;
    }

    public void setRepaymentPeriod(Integer repaymentPeriod) {
        this.repaymentPeriod = repaymentPeriod;
    }

    public String getOtherNeeds() {
        return otherNeeds;
    }

    public void setOtherNeeds(String otherNeeds) {
        this.otherNeeds = otherNeeds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
