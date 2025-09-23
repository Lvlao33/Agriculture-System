package com.farmporject.backend.expert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 专家模块-专家信息
 * 路由前缀：/api/experts
 * - GET /api/experts 专家列表
 * - GET /api/experts/{id} 专家详情
 */
@RestController
@RequestMapping("/api/experts")
public class ExpertController {
    /**
     * 专家列表
     * 示例：GET /api/experts
     */
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("experts");
    }

    /**
     * 专家详情
     * 示例：GET /api/experts/123
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        return ResponseEntity.ok().body(id);
    }
}
