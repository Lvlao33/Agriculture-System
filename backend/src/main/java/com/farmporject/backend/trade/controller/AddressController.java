package com.farmporject.backend.trade.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 交易模块-收发货地址
 * 路由前缀：/api/trade/addresses
 * - GET /api/trade/addresses 地址列表
 * - POST /api/trade/addresses 新增地址
 */
@RestController
@RequestMapping("/api/trade/addresses")
public class AddressController {

    /** 地址列表 */
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("addresses");
    }

    /** 新增地址 */
    @PostMapping
    public ResponseEntity<?> create() {
        return ResponseEntity.status(201).body("create address");
    }
}
