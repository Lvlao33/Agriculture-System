package com.farmporject.backend.expert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 知识模块-农业知识
 * 路由前缀：/api/knowledge
 * - GET /api/knowledge 知识列表
 * - POST /api/knowledge 新增知识
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    /**
     * 知识列表
     * 示例：GET /api/knowledge
     */
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("knowledge");
    }

    /**
     * 新增知识
     * 示例：POST /api/knowledge
     */
    @PostMapping
    public ResponseEntity<?> create() {
        return ResponseEntity.status(201).body("knowledge created");
    }
}
