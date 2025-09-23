package com.farmporject.backend.expert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 资讯模块-新闻/动态
 * 路由前缀：/api/news
 * - GET /api/news 新闻/动态列表
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("news");
    }
}
