package com.farmporject.backend.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;

import java.util.List;

import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.model.LoanProduct;
import com.farmporject.backend.finance.model.LoanRecord;
import com.farmporject.backend.finance.model.LoanUserStatus;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role", nullable = false)
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

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
