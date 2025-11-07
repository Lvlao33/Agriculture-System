package com.farmporject.backend.finance.repository;

import com.farmporject.backend.finance.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Loan entity.
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    // 按需增加数据库操作

    // 增加贷款申请 jpa已有
    // Loan save(Loan loan);

    // 根据农户ID查找贷款记录 jpa已有
    // List<Loan> findById(Long farmerId);
}
