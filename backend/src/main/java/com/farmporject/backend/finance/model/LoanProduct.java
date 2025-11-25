package com.farmporject.backend.finance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.farmporject.backend.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "loan_products")
public class LoanProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_product_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "product_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    enum ProductStatus {
        SALE,
        DELETED
    }

    @Column(name = "bank", nullable = false)
    private String bank;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "term", nullable = false)
    private Integer term;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    @Column(name = "fastest_disbursement", nullable = false)
    private String fastestDisbursement;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "tags", nullable = false)
    private String tags;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonIgnore
    private User staff;

    @OneToMany(mappedBy = "loanProduct")
    @JsonIgnore
    private List<Loan> loan;

    public LoanProduct() {
    }

    // 农户端查询构造
    public LoanProduct(Long id, String name, String bank, BigDecimal amount, Integer term, BigDecimal rate,
            String fastestDisbursement) {
        this.id = id;
        this.name = name;
        this.bank = bank;
        this.amount = amount;
        this.term = term;
        this.rate = rate;
        this.fastestDisbursement = fastestDisbursement;
    }

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getFastestDisbursement() {
        return fastestDisbursement;
    }

    public void setFastestDisbursement(String fastestDisbursement) {
        this.fastestDisbursement = fastestDisbursement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public User getStaff() {
        return staff;
    }

    public void setStaff(User staff) {
        this.staff = staff;
    }

    public List<Loan> getLoan() {
        return loan;
    }

    public void setLoan(List<Loan> loan) {
        this.loan = loan;
    }

    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

    @Override
    public String toString() {
        return "LoanProduct [id=" + id + ", name=" + name + ", bank=" + bank + ", amount=" + amount + ", term=" + term
                + ", rate=" + rate + ", fastestDisbursement=" + fastestDisbursement + ", description=" + description
                + ", tags=" + tags + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", deletedAt="
                + deletedAt + ", staff=" + staff + ", loan=" + loan + "]";
    }
}
