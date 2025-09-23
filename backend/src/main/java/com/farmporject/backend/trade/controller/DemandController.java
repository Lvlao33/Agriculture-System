package com.farmporject.backend.trade.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 交易模块-采购需求
 * 路由前缀：/api/trade/demands
 * - GET /api/trade/demands 采购需求列表
 * - POST /api/trade/demands 发布采购需求
 */
@RestController
@RequestMapping("/api/trade/demands")
public class DemandController {

    /** 采购需求列表 */
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("list demands");
    }

    /** 发布采购需求 */
    @PostMapping
    public ResponseEntity<?> create() {
        return ResponseEntity.status(201).body("create demand");
    }
}
