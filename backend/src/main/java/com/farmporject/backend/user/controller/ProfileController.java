package com.farmporject.backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 用户-个人信息 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @GetMapping
    public ResponseEntity<?> me() {
        return ResponseEntity.ok().body("me");
    }

    @PutMapping
    public ResponseEntity<?> update() {
        return ResponseEntity.ok().body("updated");
    }
}
