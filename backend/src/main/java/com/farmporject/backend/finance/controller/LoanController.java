package com.farmporject.backend.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 融资-融资申请 */
@RestController
@RequestMapping("/api/finance/loans")
public class LoanController {
    // 填写融资申请
    @PostMapping("/apply")
    public ResponseEntity<?> apply() {
        return ResponseEntity.status(201).body("loan applied");
    }

    // 融资申请列表
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("loan list");
    }
}
