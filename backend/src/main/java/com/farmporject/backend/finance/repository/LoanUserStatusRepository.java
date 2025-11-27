package com.farmporject.backend.finance.repository;

import org.springframework.stereotype.Repository;
import com.farmporject.backend.finance.model.LoanUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface LoanUserStatusRepository extends JpaRepository<LoanUserStatus, Long> {
    List<LoanUserStatus> findByLoanId(Long loanId);
    List<LoanUserStatus> findByUser_Id(Long userId);
    LoanUserStatus findByLoan_IdAndUser_Id(Long loanId, Long userId);
}