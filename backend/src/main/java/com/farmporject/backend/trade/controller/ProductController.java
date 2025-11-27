package com.farmporject.backend.trade.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 交易模块-商品管理
 * 路由前缀�?/api/trade/products
 * - GET /api/trade/products 商品列表
 * - POST /api/trade/products 新建商品
 * - GET /api/trade/products/{id} 商品详情
 */
@RestController
@RequestMapping("/api/trade/products")
public class ProductController {

    /** 商品列表 */
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("list products");
    }

    /** 新建商品 */
    @PostMapping
    public ResponseEntity<?> create() {
        return ResponseEntity.status(201).body("create product");
    }

    /** 商品详情 */
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        return ResponseEntity.ok().body(id);
    }
}
