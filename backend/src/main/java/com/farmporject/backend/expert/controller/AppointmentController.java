package com.farmporject.backend.expert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 专家模块-预约
 * 路由前缀：/api/appointments
 * - GET /api/appointments 预约列表（我的/待处理）
 * - POST /api/appointments 创建预约
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    /**
     * 预约列表
     * 示例：GET /api/appointments
     */
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("appointments");
    }

    /**
     * 创建预约
     * 示例：POST /api/appointments/add
     */
    @PostMapping("/add")
    public ResponseEntity<?> create() {
        return ResponseEntity.status(201).body("appointment created");
    }
}
