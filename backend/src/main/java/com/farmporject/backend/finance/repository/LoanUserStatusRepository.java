package com.farmporject.backend.finance.repository;

import org.springframework.stereotype.Repository;
import com.farmporject.backend.finance.model.LoanUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface LoanUserStatusRepository extends JpaRepository<LoanUserStatus, Long> {
    // 使用 JOIN FETCH 一次性加载所有关联数据
    @Query("SELECT DISTINCT lus FROM LoanUserStatus lus " +
            "JOIN FETCH lus.loan l " +
            "JOIN FETCH l.loanProduct " +
            "JOIN FETCH l.staff " +
            "LEFT JOIN FETCH l.loanUserStatuses " +
            "WHERE lus.user.id = :userId")
    List<LoanUserStatus> findByUserIdWithAllRelations(@Param("userId") Long userId);
}