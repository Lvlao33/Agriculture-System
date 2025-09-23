package com.farmporject.backend.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 融资-融资申请 */
@RestController
@RequestMapping("/api/finance/loans")
public class LoanController {
    @PostMapping("/apply")
    public ResponseEntity<?> apply() {
        return ResponseEntity.status(201).body("loan applied");
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("loan list");
    }
}
