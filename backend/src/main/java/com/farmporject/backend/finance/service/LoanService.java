package com.farmporject.backend.finance.service;

import org.springframework.stereotype.Service;
import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.repository.LoanRepository;

// 功能：贷款业务逻辑（申请处理、验证、保存、审批交互等）。
@Service
public class LoanService {

    private final LoanRepository repo;

    public LoanService(LoanRepository loanRepository) {
        this.repo = loanRepository;
    }

    /**
     * 提交贷款申请
     * 
     * @param loan 贷款申请信息
     * @return true 如果成功保存到数据库，false 如果保存失败
     */
    public boolean apply(Loan loan) {
        try {
            // 确保必要字段不为空
            if (loan == null || loan.getLoanAmount() == null || loan.getFarmerId() == null) {
                return false;
            }

            // 设置初始状态为已申请
            loan.setStatus(Loan.LoanStatus.APPLIED.name());
            // 设置申请时间
            loan.setApplicationDate(java.time.LocalDateTime.now());

            // 保存到数据库并确认是否成功
            Loan savedLoan = repo.save(loan);
            // 如果保存成功，savedLoan 会有 ID，返回 true
            return savedLoan.getId() != null;

        } catch (Exception e) {
            // 实际项目中应该使用日志记录异常
            // logger.error("Failed to save loan application", e);
            return false;
        }
    }

}
