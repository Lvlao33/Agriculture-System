package com.farmporject.backend.finance.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.farmporject.backend.finance.dto.*;
import com.farmporject.backend.finance.model.*;
import com.farmporject.backend.finance.repository.*;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// 功能：贷款业务逻辑（申请处理、验证、保存、审批交互等）。
@Service
public class LoanService {

    private final LoanRepository repo;
    private final UserRepository userRepository;
    private final LoanProductRepository loanProductRepository;
    private final LoanUserStatusRepository loanUserStatusRepository;
    private final LoanRecordRepository loanRecordRepository;

    private final LoanFileService loanFileService;

    @Autowired
    public LoanService(LoanRepository loanRepository, LoanFileService loanFileService, UserRepository userRepository,
            LoanProductRepository loanProductRepository, LoanUserStatusRepository loanUserStatusRepository,
            LoanRecordRepository loanRecordRepository) {
        this.repo = loanRepository;
        this.loanFileService = loanFileService;
        this.userRepository = userRepository;
        this.loanProductRepository = loanProductRepository;
        this.loanUserStatusRepository = loanUserStatusRepository;
        this.loanRecordRepository = loanRecordRepository;
    }

    /**
     * 提交贷款申请
     * 
     * @param loanDTO    前端传回的贷款申请信息
     * @param operatorId 操作者ID
     * @return Loan 如果成功保存到数据库，null 如果保存失败
     */
    public Loan apply(LoanDTO loanDTO, Long operatorId) {
        try {

            // 确保必要字段不为空
            if (loanDTO == null || loanDTO.getLoanAmount() == null || loanDTO.getUserIds() == null
                    || operatorId == null) {
                return null;
            }

            // 获取操作者
            User operator = userRepository.findById(operatorId)
                    .orElseThrow(() -> {
                        return new RuntimeException("操作者不存在");
                    });

            /**
             * 从DTO转到Entity
             *
             * 获得普通用户
             * 可能不止一个用户
             * 使用哈希表防重复
             */
            List<User> users = new ArrayList<>();
            for (Long userId : loanDTO.getUserIds()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> {
                            return new RuntimeException("用户不存在");
                        });
                users.add(user);
            }

            // 获取贷款产品
            if (loanDTO.getLoanProductId() != null) {
                LoanProduct loanProduct = loanProductRepository.findById(loanDTO.getLoanProductId())
                        .orElseThrow(() -> {
                            return new ResponseStatusException(HttpStatus.NOT_FOUND, "贷款产品不存在");
                        });

                // 构造贷款申请实体
                Loan loan = new Loan();
                loan.setLoanAmount(loanDTO.getLoanAmount());
                loan.setLoanPurpose(loanDTO.getLoanPurpose());
                loan.setLoanTermMonths(loanDTO.getLoanTermMonths());
                loan.setInterestRate(loanDTO.getInterestRate());
                loan.setRemark(loanDTO.getRemark());

                // 默认值设置
                loan.setStatus(Status.CREATED);
                loan.setApplicationDate(java.time.LocalDateTime.now());
                loan.setUpdateDate(java.time.LocalDateTime.now());
                loan.setDisbursementDate(null);
                loan.setRepaymentDueDate(null);

                // 先保存默认值，免受表关联关系影响
                Loan savedLoan = repo.save(loan);

                // 新建用户状态跟踪记录
                // 设置与用户状态跟踪记录的表关联
                for (User user : users) {
                    LoanUserStatus loanUserStatus = new LoanUserStatus();
                    loanUserStatus.setUser(user);
                    loanUserStatus.setLoan(loan);
                    loanUserStatus.setStatus(Status.CREATED);
                    loanUserStatusRepository.save(loanUserStatus);
                    loan.getLoanUserStatuses().add(loanUserStatus);
                }

                // 新建贷款操作记录
                // 设置与贷款操作记录的表关联
                LoanRecord loanRecord = new LoanRecord();
                loanRecord.setLoan(loan);
                loanRecord.setRecordDate(java.time.LocalDateTime.now());
                loanRecord.setRecordDetails("贷款申请");
                loanRecord.setApplyStatus(Status.CREATED);
                loanRecord.setUser(operator);
                loanRecordRepository.save(loanRecord);
                loan.getLoanRecords().add(loanRecord);

                // 设置与贷款产品的表关联
                loan.setLoanProduct(loanProduct);

                // file表和staff之后的业务再与loan进行关联

                // 保存到数据库并确认是否成功
                savedLoan = repo.save(loan);
                // 如果保存成功，savedLoan 会有 ID，返回 true
                return savedLoan;
            }

        } catch (Exception e) {
            // 实际项目中应该使用日志记录异常
            // logger.error("Failed to save loan application", e);
            return null;
        }

        return null;
    }

    /**
     * 上传贷款相关文件
     * 
     * @param loanId 贷款ID
     * @param file   文件
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
     * 由银行侧来修改
     * 
     * @param Status 新的状态
     * @param loanId 贷款ID
     * @return true 如果数据库贷款申请记录状态成功修改，false 如果失败
     */
    public boolean submitByLoanId(Long loanId, Status status) {
        // 先查询贷款申请信息
        try {
            if (loanId != null) {
                Loan loan = repo.findById(loanId).orElseThrow(() -> new RuntimeException("贷款申请不存在"));
                // 若存在，根据传入函数的status修改loan
                if (loan != null) {
                    loan.setStatus(status);
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

    /**
     * 修改贷款申请资料
     * 
     * @param loan 贷款申请信息
     * @return true 如果成功保存到数据库，false 如果保存失败
     */
    public boolean update(Loan loan) {
        try {
            // 确保必要字段不为空
            if (loan == null || loan.getId() == null) {
                return false;
            }

            // 确保存在
            repo.findById(loan.getId()).orElseThrow(() -> new RuntimeException("贷款申请不存在"));

            // 设置更新时间
            loan.setUpdateDate(java.time.LocalDateTime.now());

            // 保存到数据库并确认是否成功
            Loan savedLoan = repo.save(loan);
            // 如果保存成功，savedLoan 会有 ID，返回 true
            return savedLoan.getId() != null;
        } catch (Exception e) {
            // 实际项目中应该使用日志记录异常
            // logger.error("Failed to update loan application", e);
            return false;
        }
    }
}