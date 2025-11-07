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
}
