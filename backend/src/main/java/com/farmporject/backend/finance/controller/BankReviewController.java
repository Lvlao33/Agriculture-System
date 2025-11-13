package com.farmporject.backend.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmporject.backend.finance.service.LoanService;
import com.farmporject.backend.finance.model.Loan;

/** 银行端-融资审批 */
@RestController
@RequestMapping("/api/finance/bank-review")
public class BankReviewController {
    private final LoanService loanService;

    public BankReviewController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/applications")
    public ResponseEntity<?> listApps() {
        return ResponseEntity.ok().body("applications");
    }

    // 该业务完成后续需要身份认证
    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable String id) {
        loanService.submitByLoanId(Long.parseLong(id), Loan.LoanStatus.APPROVED);
        return ResponseEntity.ok().body("approved " + id);
    }

    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable String id) {
        loanService.submitByLoanId(Long.parseLong(id), Loan.LoanStatus.REJECTED);
        return ResponseEntity.ok().body("rejected " + id);
    }
}
