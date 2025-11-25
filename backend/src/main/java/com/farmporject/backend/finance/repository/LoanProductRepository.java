package com.farmporject.backend.finance.repository;

import com.farmporject.backend.finance.model.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for LoanProduct entity.
 */
@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
}