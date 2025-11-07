package com.farmporject.backend.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.farmporject.backend.finance.service.LoanService;
import com.farmporject.backend.finance.model.Loan;

/** 融资-融资申请 */
@RestController
@RequestMapping("/api/finance/loans")
public class LoanController {

    private final LoanService loanService;

    // Spring 会自动注入 LoanService Bean
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // 填写融资申请
    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody Loan loan) {
        // 调用服务层完成融资申请
        if (loanService.apply(loan)) {
            return ResponseEntity.ok().body("apply success");
        } else {
            return ResponseEntity.status(400).body("apply failed");
        }
    }

    // 提交融资证明资料文件
    @PostMapping("/{loan_id}/upload")
    public ResponseEntity<?> uploadFile(@PathVariable("loan_id") Long loanId,
            @RequestParam("file") MultipartFile file) {
        // 调用服务层完成提交融资证明资料
        try {
            if (loanService.uploadFileByLoanId(loanId, file)) {
                return ResponseEntity.ok().body("upload file success");
            } else {
                return ResponseEntity.status(400).body("upload file failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("upload file exception: " + e.getMessage());
        }
    }

    // 融资申请列表
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("loan list");
    }
}
