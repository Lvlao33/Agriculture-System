package com.farmporject.backend.user.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import java.util.List;

import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.model.LoanProduct;
import com.farmporject.backend.finance.model.LoanRecord;
import com.farmporject.backend.finance.model.LoanUserStatus;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(nullable = false, length = 128)
    private String passwordHash;

    @Column(length = 64)
    private String nickname;

    @Column(length = 256)
    private String avatar;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "role")
    private String role; // finance板块中："FARMER" 或 "STAFF"

    @OneToMany(mappedBy = "user")
    private List<LoanUserStatus> loanUserStatuses; // 农户与贷款的多对多关系，带有状态信息

    @OneToMany(mappedBy = "user")
    private List<LoanRecord> loanrRecords; // 所有用户与 LoanRecord 的一对多关系

    @OneToMany(mappedBy = "staff")
    private List<LoanProduct> loanProducts; // 工作人员 与 LoanProduct 的一对多关系

    @OneToMany(mappedBy = "staff")
    private List<Loan> assignedLoans; // 工作人员与 Loan 的一对多关系

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /* finance：staff */

    public List<Loan> getAssignedLoans() {
        return assignedLoans;
    }

    public void setAssignedLoans(List<Loan> assignedLoans) { // 工作人员与 Loan 的一对多关系
        this.assignedLoans = assignedLoans;
    }

    public List<LoanProduct> getLoanProducts() {
        return loanProducts;
    }

    public void setLoanProducts(List<LoanProduct> loanProducts) { // 所有用户与 LoanProduct 的一对多关系
        this.loanProducts = loanProducts;
    }

    /* finance : 所有user */

    public List<LoanRecord> getLoanrRecords() {
        return loanrRecords;
    }

    public void setLoanrRecords(List<LoanRecord> loanrRecords) { // 所有用户与 LoanRecord 的一对多关系
        this.loanrRecords = loanrRecords;
    }

    /* finance : farmer */

    public List<LoanUserStatus> getLoanUserStatuses() {
        return loanUserStatuses;
    }

    public void setLoanUserStatuses(List<LoanUserStatus> loanUserStatuses) { // 用户与贷款的多对多关系，带有状态信息
        this.loanUserStatuses = loanUserStatuses;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
