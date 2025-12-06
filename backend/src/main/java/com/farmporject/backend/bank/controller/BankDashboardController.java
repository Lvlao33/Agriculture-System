package com.farmporject.backend.bank.controller;

import com.farmporject.backend.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bank/dashboard")
public class BankDashboardController {

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("pendingLoans", 12);
        data.put("matchedFarmers", 8);
        data.put("riskAlerts", 2);
        data.put("totalCredit", 56_000_000);
        data.put("approvalRate", "92%");
        data.put("avgProcessingTime", "1.6 �?");
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/loans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> loans() {
        List<Map<String, Object>> list = Arrays.asList(
                buildLoan("L001", "张三农业合作�?", "智慧农业贷款", 1_200_000, "24 个月", "09:15", "urgent", "紧�?"),
                buildLoan("L002", "李四农场", "农机设备采购", 600_000, "18 个月", "08:40", "normal", "正常"),
                buildLoan("L003", "王五企业", "饲料采购周转", 350_000, "12 个月", "昨天 17:20", "review", "审核�?")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> matches() {
        List<Map<String, Object>> list = Arrays.asList(
                buildMatch("M001", "优质农业企业", "蔬菜加工", 800_000, "A�?", "绿色农业银行"),
                buildMatch("M002", "清水水产", "小龙虾养�?", 500_000, "B+�?", "现代农业银行")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> alerts() {
        List<Map<String, Object>> list = Arrays.asList(
                buildAlert("A001", "账户余额异常", "李四农场 7 天内现金流动异常，需要复核贷款项�?", "紧�?"),
                buildAlert("A002", "抵押物价值下�?", "王五企业抵押物价值下降，需要重新评�?", "警告")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> notifications() {
        List<Map<String, String>> list = Arrays.asList(
                buildNotice("N001", "总部通知�?11 月农户贷款政策更�?", "1 小时�?"),
                buildNotice("N002", "系统维护�?11/30 01:00-03:00 暂停服务", "今天")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    private Map<String, Object> buildLoan(String id, String farmer, String product,
                                          int amount, String term, String time, String level, String levelText) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("farmer", farmer);
        map.put("product", product);
        map.put("amount", amount);
        map.put("term", term);
        map.put("time", time);
        map.put("level", level);
        map.put("levelText", levelText);
        return map;
    }

    private Map<String, Object> buildMatch(String id, String farmer, String crop,
                                           int need, String credit, String bank) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("farmer", farmer);
        map.put("crop", crop);
        map.put("need", need);
        map.put("credit", credit);
        map.put("bank", bank);
        return map;
    }

    private Map<String, Object> buildAlert(String id, String title, String desc, String level) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("desc", desc);
        map.put("level", level);
        return map;
    }

    private Map<String, String> buildNotice(String id, String content, String time) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("content", content);
        map.put("time", time);
        return map;
    }
}
