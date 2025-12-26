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

    private final com.farmporject.backend.finance.service.LoanProductService loanProductService;
    private final com.farmporject.backend.expert.service.ExpertService expertService;
    private final com.farmporject.backend.expert.service.AnswerService answerService;
    private final com.farmporject.backend.expert.service.KnowledgeService knowledgeService;
    private final com.farmporject.backend.trade.service.ProductService productService;

    public HomeController(
            com.farmporject.backend.finance.service.LoanProductService loanProductService,
            com.farmporject.backend.expert.service.ExpertService expertService,
            com.farmporject.backend.expert.service.AnswerService answerService,
            com.farmporject.backend.expert.service.KnowledgeService knowledgeService,
            com.farmporject.backend.trade.service.ProductService productService) {
        this.loanProductService = loanProductService;
        this.expertService = expertService;
        this.answerService = answerService;
        this.knowledgeService = knowledgeService;
        this.productService = productService;
    }

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

    @GetMapping("/api/home/data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHomeData() {
        Map<String, Object> data = new HashMap<>();

        // 1. 新闻轮播（静态数据）
        java.util.List<Map<String, String>> news = java.util.Arrays.asList(
                createNews("1", "农业农村部：今年粮食产量将再创历史新高", "2024-12-25"),
                createNews("2", "金融支持乡村振兴力度加大，涉农贷款余额持续增长", "2024-12-24"),
                createNews("3", "智慧农业技术助力冬小麦田间管理", "2024-12-23"),
                createNews("4", "返乡创业创新蔚然成风，乡村吸引力不断增强", "2024-12-22"));
        data.put("news", news);

        // 2. 金融产品（前10个）
        data.put("financeProducts", loanProductService.getProductsForFarmer().stream().limit(10).toList());

        // 3. 专家一栏（前10个）
        data.put("experts", expertService.getAllExperts().stream().limit(10).toList());

        // 4. 专家问答（前10个，含问题）
        data.put("qas", answerService.getRecentAnswers());

        // 5. 农业知识（前10条）
        data.put("knowledge", knowledgeService.getRecentKnowledge());

        // 6. 助农电商（前10个）
        data.put("tradeProducts", productService.getAvailableProducts().stream().limit(10).toList());

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    private Map<String, String> createNews(String id, String title, String date) {
        Map<String, String> n = new HashMap<>();
        n.put("id", id);
        n.put("title", title);
        n.put("date", date);
        return n;
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> data = new HashMap<>();
        data.put("status", "UP");
        data.put("service", "农业系统后端服务");
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
