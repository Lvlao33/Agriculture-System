package com.farmporject.backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 用户-鉴权 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/login")
    public ResponseEntity<?> login() {
        return ResponseEntity.ok().body("token");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register() {
        return ResponseEntity.status(201).body("user created");
    }
}
