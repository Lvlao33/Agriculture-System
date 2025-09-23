package com.farmporject.backend.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 融资-产品列表 */
@RestController
@RequestMapping("/api/finance/loan-products")
public class LoanProductController {
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("loan products");
    }
}
