package com.farmporject.backend.finance.repository;

import com.farmporject.backend.finance.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for Loan entity.
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    public List<Loan> findByApplicationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
