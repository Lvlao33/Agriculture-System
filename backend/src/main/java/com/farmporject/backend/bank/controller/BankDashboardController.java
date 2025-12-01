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
        data.put("avgProcessingTime", "1.6 days");
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/loans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> loans() {
        List<Map<String, Object>> list = Arrays.asList(
                buildLoan("L001", "GreenField Co-op", "Smart farming loan", 1_200_000, "24 months", "09:15", "urgent", "urgent"),
                buildLoan("L002", "SpringRain Farm", "Machinery purchase", 600_000, "18 months", "08:40", "normal", "normal"),
                buildLoan("L003", "Harvest Fruit Ltd", "Input circulation", 350_000, "12 months", "17:20", "review", "review")
        );
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> matches() {
        List<Map<String, Object>> list = Arrays.asList(
                buildMatch("M001", "Hengxin Veg Co-op", "Cold-chain upgrade", 800_000, "A", "Green Agri Bank"),
                buildMatch("M002", "Qinghe Aquatics", "Crayfish farming", 500_000, "B+", "Modern Agri Bank")
        );
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> alerts() {
        List<Map<String, Object>> list = Arrays.asList(
                buildAlert("A001", "Cash flow warning", "SpringRain Farm cash flow fluctuated in 7 days, please review plan", "urgent"),
                buildAlert("A002", "Collateral value drop", "Harvest Fruit collateral value reduced, re-evaluate immediately", "alert")
        );
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> notifications() {
        List<Map<String, String>> list = Arrays.asList(
                buildNotice("N001", "Head office: Nov subsidy policy update", "1 hour ago"),
                buildNotice("N002", "Maintenance: 11/30 01:00-03:00 service pause", "today")
        );
        return ResponseEntity.ok(ApiResponse.ok(list));
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
