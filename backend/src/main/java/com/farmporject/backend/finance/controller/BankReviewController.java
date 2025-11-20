package com.farmporject.backend.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmporject.backend.finance.service.LoanService;
import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.model.Status;    

import java.time.LocalDateTime;

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
        // 默认查距离现在30天内的申请
        return ResponseEntity.ok()
                .body(loanService.findLoanListByTime(LocalDateTime.now().minusDays(30), LocalDateTime.now()));
    }

    // 该业务完成后续需要身份认证
    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable String id) {
        if (loanService.submitByLoanId(Long.parseLong(id), Status.APPROVED)) {
            return ResponseEntity.ok().body("approved " + id);
        } else {
            return ResponseEntity.badRequest().body("failed to approve " + id);
        }
    }

    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable String id) {
        if (loanService.submitByLoanId(Long.parseLong(id), Status.REJECTED)) {
            return ResponseEntity.ok().body("rejected " + id);
        } else {
            return ResponseEntity.badRequest().body("failed to reject " + id);
        }
    }
}
