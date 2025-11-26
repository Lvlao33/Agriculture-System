package com.farmporject.backend.finance.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.farmporject.backend.finance.repository.LoanRecordRepository;
import com.farmporject.backend.finance.model.LoanRecord;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.model.Status;
import java.time.LocalDateTime;

/**
 * LoanRecordService
 * 1. 每次用户对贷款进行操作后都要创建record
 * 2. 工作人员的操作会改变userLoanStatus
 * 3. 与某一loan关联的所有userLoanStatus记录更新要检查loan是否需要更新
 */
@Service
public class LoanRecordService {

    private final LoanRecordRepository loanRecordRepository;

    @Autowired
    public LoanRecordService(LoanRecordRepository loanRecordRepository) {
        this.loanRecordRepository = loanRecordRepository;
    }

    public LoanRecord createRecord(Loan loan, User operator, String action, Status status) {
        LoanRecord loanRecord = new LoanRecord();
        loanRecord.setLoan(loan);
        loanRecord.setUser(operator);
        loanRecord.setRecordDate(LocalDateTime.now());
        loanRecord.setRecordDetails(operator.getNickname() + action);
        loanRecord.setApplyStatus(status);
        return loanRecordRepository.save(loanRecord);
    }

}
