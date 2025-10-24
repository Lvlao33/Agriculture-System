package com.farmporject.backend.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmporject.backend.finance.service.LoanService;

/** 融资-融资申请 */
@RestController
@RequestMapping("/api/finance/loans")
public class LoanController {
    // 填写融资申请
    @PostMapping("/apply")
    public ResponseEntity<?> apply() {
        // 调用服务层完成融资申请
        LoanService loanService = new LoanService();
        if (loanService.apply()) {
            return ResponseEntity.ok().body("apply success");
        }
        return ResponseEntity.status(400).body("apply failed");
    }

    // 融资申请列表
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("loan list");
    }
}
