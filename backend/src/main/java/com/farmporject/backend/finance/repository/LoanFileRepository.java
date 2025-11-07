package com.farmporject.backend.finance.repository;

import com.farmporject.backend.finance.model.LoanFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for LoanFile entity.
 */
@Repository
public interface LoanFileRepository extends JpaRepository<LoanFile, Long> {
    // 上传文件 jpa自带
    // LoanFile save(LoanFile loanFile);
}
