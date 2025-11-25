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

    @ManyToOne
    @JsonIgnore
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
