package com.farmporject.backend.bank.controller;

import com.farmporject.backend.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
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
        data.put("avgProcessingTime", "1.6 澶?");
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/loans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> loans() {
        List<Map<String, Object>> list = Arrays.asList(
                buildLoan("L001", "寮犱笁鍐滀笟鍚堜綔绀?", "鏅烘収鍐滀笟璐锋", 1_200_000, "24 涓湀", "09:15", "urgent", "绱ф€?"),
                buildLoan("L002", "鏉庡洓鍐滃満", "鍐滄満璁惧閲囪喘", 600_000, "18 涓湀", "08:40", "normal", "姝ｅ父"),
                buildLoan("L003", "鐜嬩簲浼佷笟", "楗叉枡閲囪喘鍛ㄨ浆", 350_000, "12 涓湀", "鏄ㄥぉ 17:20", "review", "瀹℃牳涓?")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> matches() {
        List<Map<String, Object>> list = Arrays.asList(
                buildMatch("M001", "浼樿川鍐滀笟浼佷笟", "钄彍鍔犲伐", 800_000, "A绾?", "缁胯壊鍐滀笟閾惰"),
                buildMatch("M002", "娓呮按姘翠骇", "灏忛緳铏惧吇娈?", 500_000, "B+绾?", "鐜颁唬鍐滀笟閾惰")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> alerts() {
        List<Map<String, Object>> list = Arrays.asList(
                buildAlert("A001", "璐︽埛浣欓寮傚父", "鏉庡洓鍐滃満 7 澶╁唴鐜伴噾娴佸姩寮傚父锛岄渶瑕佸鏍歌捶娆鹃」鐩?", "绱ф€?"),
                buildAlert("A002", "鎶垫娂鐗╀环鍊间笅闄?", "鐜嬩簲浼佷笟鎶垫娂鐗╀环鍊间笅闄嶏紝闇€瑕侀噸鏂拌瘎浼?", "璀﹀憡")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> notifications() {
        List<Map<String, String>> list = Arrays.asList(
                buildNotice("N001", "鎬婚儴閫氱煡锛?11 鏈堝啘鎴疯捶娆炬斂绛栨洿鏂?", "1 灏忔椂鍓?"),
                buildNotice("N002", "绯荤粺缁存姢锛?11/30 01:00-03:00 鏆傚仠鏈嶅姟", "浠婂ぉ")
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
