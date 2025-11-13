package com.farmporject.backend.finance.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.repository.LoanRepository;

import java.time.LocalDateTime;
import java.util.List;

// 功能：贷款业务逻辑（申请处理、验证、保存、审批交互等）。
@Service
public class LoanService {

    private final LoanRepository repo;
    private final LoanFileService loanFileService;

    public LoanService(LoanRepository loanRepository, LoanFileService loanFileService) {
        this.repo = loanRepository;
        this.loanFileService = loanFileService;
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
            loan.setStatus(Loan.LoanStatus.REVIEWING.name());
            // 设置申请时间
            loan.setApplicationDate(java.time.LocalDateTime.now());
            loan.setUpdateDate(java.time.LocalDateTime.now());

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

    /**
     * 提交融资证明资料文件
     * 
     * @param loanId 贷款ID
     * @param file   用户上传的文件
     * @return true 如果成功处理文件上传，false 如果失败
     */
    public boolean uploadFileByLoanId(Long loanId, MultipartFile file) throws Exception {
        // 先查询贷款申请信息
        if (loanId == null) {
            throw new Exception("贷款ID不能为空");
        }
        Loan loan = repo.findById(loanId).orElseThrow(() -> new Exception("贷款申请不存在"));

        // 调用文件上传服务，保存文件到服务器
        try {
            return loanFileService.uploadFile(loan, file);
        } catch (Exception e) {
            throw new Exception("文件上传失败: " + e.getMessage());
        }

    }

    /**
     * 修改贷款申请状态
     * 
     * @param status 新的状态
     * @param loanId 贷款ID
     * @return true 如果数据库贷款申请记录状态成功修改，false 如果失败
     */
    public boolean submitByLoanId(Long loanId, Loan.LoanStatus status) {
        // 先查询贷款申请信息
        try {
            if (loanId != null) {
                Loan loan = repo.findById(loanId).orElseThrow(() -> new RuntimeException("贷款申请不存在"));
                // 若存在，根据传入函数的status修改loan
                if (loan != null) {
                    loan.setStatus(status.name());
                    loan.setUpdateDate(LocalDateTime.now());
                    return repo.save(loan).getId() != null;
                }
            } else {
                throw new RuntimeException("贷款ID不能为空");
            }
        } catch (Exception e) {
            // 实际项目中应该使用日志记录异常
            // logger.error("Failed to submit loan application", e);
        }
        return false;
    }

    /**
     * 按时段查询贷款列表
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 贷款列表
     */
    public List<Loan> findLoanListByTime(LocalDateTime startDate, LocalDateTime endDate) {
        return repo.findByApplicationDateBetween(startDate, endDate);
    }
}
//