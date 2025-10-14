package com.farmporject.backend.finance.repository;

import com.farmporject.backend.finance.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Loan entity.
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    // 按需增加数据库操作

    /** 查找某农户的所有贷款记录 */
    List<Loan> findByFarmerId(Long farmerId);

    /** 根据状态查找贷款记录（例如 PENDING/APPROVED/REJECTED） */
    List<Loan> findByStatus(String status);
}
