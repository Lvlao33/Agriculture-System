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
@CrossOrigin(origins = "*")
public class BankDashboardController {

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("pendingLoans", 12);
        data.put("matchedFarmers", 8);
        data.put("riskAlerts", 2);
        data.put("totalCredit", 56_000_000);
        data.put("approvalRate", "92%");
        data.put("avgProcessingTime", "1.6 天");
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/loans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> loans() {
        List<Map<String, Object>> list = Arrays.asList(
                buildLoan("L001", "张家湾合作社", "智慧温室升级", 1_200_000, "24 个月", "09:15", "urgent", "高优"),
                buildLoan("L002", "吴川果农", "柑橘冷链扩建", 600_000, "18 个月", "08:40", "normal", "常规"),
                buildLoan("L003", "青川牧业", "饲料采购周转", 350_000, "12 个月", "昨天 17:20", "review", "复核")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> matches() {
        List<Map<String, Object>> list = Arrays.asList(
                buildMatch("M001", "黎川茶业", "白茶加工", 800_000, "A级", "绿色农业银行"),
                buildMatch("M002", "广水水产", "小龙虾育苗", 500_000, "B+级", "乡村振兴银行")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> alerts() {
        List<Map<String, Object>> list = Arrays.asList(
                buildAlert("A001", "账户流动性异常", "吴川果农近 7 日现金流波动，需复核账目", "复核"),
                buildAlert("A002", "抵押物估值到期", "青川牧业抵押地块需重新评估", "提醒")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> notifications() {
        List<Map<String, String>> list = Arrays.asList(
                buildNotice("N001", "总部发布：11 月涉农贷款政策更新", "1 小时前"),
                buildNotice("N002", "系统维护：11/30 01:00-03:00 暂停服务", "昨天")
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


