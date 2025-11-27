package com.farmporject.backend.expert.controller;

import com.farmporject.backend.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expert/dashboard")
@CrossOrigin(origins = "*")
public class ExpertDashboardController {

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("pendingQuestions", 5);
        data.put("urgentCount", 2);
        data.put("todayAppointments", 3);
        data.put("appointmentSummary", "视频 2 · 线下 1");
        data.put("publishedKnowledge", 7);
        data.put("knowledgeTrend", "阅读量 +18%");
        data.put("expertScore", 4.9);
        data.put("reviewCount", 126);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> questions() {
        List<Map<String, Object>> list = Arrays.asList(
                buildQuestion("q1", "紧急", "柑橘叶片发黄如何处理？", "赣南果农", "09:12", "待回复", "pending"),
                buildQuestion("q2", "高优", "大棚番茄卷叶是否缺钙？", "寿光合作社", "08:40", "待复核", "review"),
                buildQuestion("q3", "常规", "水稻穗期病害防控方案", "洞庭湖农场", "昨天 21:05", "已回复", "done")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> appointments() {
        List<Map<String, Object>> list = Arrays.asList(
                buildAppointment("a1", "10:30", "视频咨询", "昌吉番茄基地", "肥水管理", "即将开始", "review"),
                buildAppointment("a2", "14:00", "现场指导", "平谷大桃合作社", "病虫害监测", "需出行", "pending"),
                buildAppointment("a3", "19:30", "群直播答疑", "平台入驻农户", "秋冬保温", "已确认", "done")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/knowledge")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> knowledge() {
        List<Map<String, Object>> list = Arrays.asList(
                buildKnowledge("k1", "小麦纹枯病绿色防控指南", "已发布", 3200, "通过审核", "done"),
                buildKnowledge("k2", "设施蔬菜温湿调控技巧", "待审核", 0, "审核中", "review"),
                buildKnowledge("k3", "农机检修保养月历", "草稿", 0, "完善中", "pending")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> notifications() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String, String>> list = Arrays.asList(
                buildNotification("n1", "平台推送：11 月农情监测报告上线", formatter.format(LocalDateTime.now())),
                buildNotification("n2", "预约提醒：请确认 12/01 田间指导", "昨天"),
                buildNotification("n3", "知识审核：有 2 篇内容待编辑", "2 天前")
        );
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    private Map<String, Object> buildQuestion(String id, String priority, String title,
                                              String source, String time, String statusText, String statusClass) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("priority", priority);
        map.put("title", title);
        map.put("source", source);
        map.put("time", time);
        map.put("statusText", statusText);
        map.put("statusClass", statusClass);
        return map;
    }

    private Map<String, Object> buildAppointment(String id, String time, String type,
                                                 String client, String topic, String statusText, String statusClass) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("time", time);
        map.put("type", type);
        map.put("client", client);
        map.put("topic", topic);
        map.put("statusText", statusText);
        map.put("statusClass", statusClass);
        return map;
    }

    private Map<String, Object> buildKnowledge(String id, String title, String status,
                                               int reads, String statusHint, String statusClass) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("status", status);
        map.put("reads", reads);
        map.put("statusHint", statusHint);
        map.put("statusClass", statusClass);
        return map;
    }

    private Map<String, String> buildNotification(String id, String content, String time) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("content", content);
        map.put("time", time);
        return map;
    }
}


