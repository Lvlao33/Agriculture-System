package com.farmporject.backend.finance.repository;

import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.model.Status;
import com.farmporject.backend.user.model.User;
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

    // 根据状态查询贷款列表
    List<Loan> findByStatus(Status status);

    // 根据状态和负责人员查询贷款列表
    List<Loan> findByStatusAndStaff(Status status, User staff);

    // 查询未分配负责人的贷款（状态为CREATED且staff为null）
    List<Loan> findByStatusAndStaffIsNull(Status status);

    // 根据多个状态和负责人员查询贷款列表
    List<Loan> findByStatusInAndStaff(List<Status> statuses, User staff);

    // 计算指定状态的贷款数量
    long countByStatus(Status status);

    // 查询指定状态且创建时间最早的三条记录
    List<Loan> findFirst3ByStatusOrderByApplicationDateAsc(Status status);
}
