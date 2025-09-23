package com.farmporject.backend.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 银行端-融资审批 */
@RestController
@RequestMapping("/api/finance/bank-review")
public class BankReviewController {
    @GetMapping("/applications")
    public ResponseEntity<?> listApps() {
        return ResponseEntity.ok().body("applications");
    }

    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable String id) {
        return ResponseEntity.ok().body("approved " + id);
    }

    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable String id) {
        return ResponseEntity.ok().body("rejected " + id);
    }
}
