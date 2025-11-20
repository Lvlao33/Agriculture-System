package com.farmporject.backend.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.farmporject.backend.finance.model.LoanRecord;

/**
 * Spring Data JPA repository for LoanRecord entity.
 */
@Repository
public interface LoanRecordRepository extends JpaRepository<LoanRecord, Long> {

}
