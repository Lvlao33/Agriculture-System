package com.farmporject.backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 用户-资产聚合 */
@RestController
@RequestMapping("/api/assets")
public class AssetController {
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("assets");
    }
}
