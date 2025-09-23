package com.farmporject.backend.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 融资-智能匹配 */
@RestController
@RequestMapping("/api/finance/matching")
public class MatchingController {
    @PostMapping("/run")
    public ResponseEntity<?> run() {
        return ResponseEntity.ok().body("matching run");
    }
}
