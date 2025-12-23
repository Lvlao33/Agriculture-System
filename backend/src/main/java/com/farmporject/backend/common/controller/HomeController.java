package com.farmporject.backend.common.controller;

import com.farmporject.backend.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 * 提供默认首页和API信息
 * CORS配置由全局CorsConfig处理
 */
@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> home() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "欢迎使用农业系统后端API服务");
        data.put("version", "1.0.0");
        data.put("status", "运行中");
        data.put("apiBaseUrl", "/api");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("用户相关", "/api/user");
        endpoints.put("专家相关", "/api/expert");
        endpoints.put("银行相关", "/api/bank");
        endpoints.put("交易相关", "/api/trade");
        endpoints.put("金融相关", "/api/finance");
        data.put("endpoints", endpoints);
        
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> data = new HashMap<>();
        data.put("status", "UP");
        data.put("service", "农业系统后端服务");
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}

