
package com.farmporject.backend.trade.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.farmporject.backend.trade.service.OrderService;

/**
 * 交易模块-订单
 * 路由前缀：/api/trade/orders
 * - GET /api/trade/orders 订单列表
 * - POST /api/trade/orders 创建订单
 */
@RestController
@RequestMapping("/api/trade/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /** 订单列表 */
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("orders");
    }

    /** 创建订单 */
    @PostMapping
    public ResponseEntity<?> create() {
        return ResponseEntity.status(201).body(orderService.createOrder());
    }
}
