package com.farmporject.backend.expert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 专家模块-问答
 * 路由前缀：/api/qa
 * - GET /api/qa/questions 问答列表
 * - POST /api/qa/questions 发起提问
 * - POST /api/qa/questions/{id}/answers 回答问题（专家端）
 */
@RestController
@RequestMapping("/api/qa")
public class QAController {
    /**
     * 问答列表
     * 示例：GET /api/qa/questions
     */
    @GetMapping("/questions")
    public ResponseEntity<?> questions() {
        return ResponseEntity.ok().body("questions");
    }

    /**
     * 发起提问
     * 示例：POST /api/qa/questions
     */
    @PostMapping("/questions")
    public ResponseEntity<?> ask() {
        return ResponseEntity.status(201).body("question created");
    }

    /**
     * 回答问题
     * 示例：POST /api/qa/questions/{id}/answers
     */
    @PostMapping("/questions/{id}/answers")
    public ResponseEntity<?> answer(@PathVariable String id) {
        return ResponseEntity.status(201).body("answered " + id);
    }
}
