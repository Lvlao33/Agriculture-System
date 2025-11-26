package com.farmporject.backend.finance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import java.time.LocalDateTime;

import com.farmporject.backend.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class LoanRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_record_id", nullable = false)
    private Long id;

    @Column(name = "record_details", length = 1000)
    private String recordDetails;

    @Column(name = "apply_status")
    @Enumerated(EnumType.STRING)
    private Status applyStatus;

    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    @JsonIgnore
    private Loan loan; // 贷款记录与 Loan 的多对一关系

    // 与所有的user 无论是农户还是承销商
    // 操作者
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 贷款记录与 User 的多对一关系

    public LoanRecord() {
    }

    public LoanRecord(String recordDetails, Status applyStatus, LocalDateTime recordDate) {
        this.recordDetails = recordDetails;
        this.applyStatus = applyStatus;
        this.recordDate = recordDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecordDetails() {
        return recordDetails;
    }

    public void setRecordDetails(String recordDetails) {
        this.recordDetails = recordDetails;
    }

    public Status getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Status applyStatus) {
        this.applyStatus = applyStatus;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
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

    @Override
    public String toString() {
        return "LoanRecord{" +
                "id=" + id +
                ", recordDetails='" + recordDetails + '\'' +
                ", applyStatus='" + applyStatus + '\'' +
                ", recordDate=" + recordDate +
                ", loan=" + loan +
                ", user=" + user +
                '}';
    }
}
