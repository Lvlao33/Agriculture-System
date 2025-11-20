package com.farmporject.backend.finance.repository;

import org.springframework.stereotype.Repository;
import com.farmporject.backend.finance.model.LoanUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LoanUserStatusRepository extends JpaRepository<LoanUserStatus, Long> {

}