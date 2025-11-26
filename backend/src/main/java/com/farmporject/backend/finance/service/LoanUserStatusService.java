package com.farmporject.backend.finance.service;

import com.farmporject.backend.finance.model.LoanUserStatus;
import com.farmporject.backend.finance.repository.LoanUserStatusRepository;

import org.springframework.stereotype.Service;

import com.farmporject.backend.finance.model.Status;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class LoanUserStatusService {
    private final LoanUserStatusRepository loanUserStatusRepository;

    @Autowired
    public LoanUserStatusService(LoanUserStatusRepository loanUserStatusRepository) {
        this.loanUserStatusRepository = loanUserStatusRepository;
    }

    public void updateLoanUserStatus(Long loanId, Long userId, Status status) {
        LoanUserStatus loanUserStatus = loanUserStatusRepository.findByLoanIdAndUserId(loanId, userId);
        if (loanUserStatus == null) {
            return;
        }
        loanUserStatus.setStatus(status);
        loanUserStatusRepository.save(loanUserStatus);
    }
}
