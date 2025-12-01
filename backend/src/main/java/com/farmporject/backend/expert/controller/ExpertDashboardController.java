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

/**
 * 专家后台仪表盘相关接口
 */
@RestController
@RequestMapping("/api/expert/dashboard")
@CrossOrigin(origins = "*")
public class ExpertDashboardController {

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> overview() {
        // 待后端开发完善，此处为模拟数据
        Map<String, Object> data = new HashMap<>();
        data.put("pendingQuestions", 5);
        data.put("urgentCount", 2);
        data.put("todayAppointments", 3);
        data.put("appointmentSummary", "Video 2 / Onsite 1");
        data.put("publishedKnowledge", 7);
        data.put("knowledgeTrend", "Readings +18%");
        data.put("expertScore", 4.9);
        data.put("reviewCount", 126);
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> questions() {
        List<Map<String, Object>> list = Arrays.asList(
                buildQuestion("q1", "urgent", "Tomato leaf spots spreading cause unknown", "Shandong farm", "09:12",
                        "waiting reply", "pending"),
                buildQuestion("q2", "urgent", "Does corn ear rot need calcium?", "NE grower", "08:40", "under review",
                        "review"),
                buildQuestion("q3", "normal", "Rice heading management tips", "Jiangxi co-op", "21:05", "answered",
                        "done"));
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> appointments() {
        List<Map<String, Object>> list = Arrays.asList(
                buildAppointment("a1", "10:30", "Video consult", "Morning Veg Co-op", "Greenhouse care",
                        "pending start", "review"),
                buildAppointment("a2", "14:00", "Onsite coaching", "Field Orchard", "Frost prevention", "in progress",
                        "pending"),
                buildAppointment("a3", "19:30", "Live class", "Open course", "Autumn rotation", "confirmed", "done"));
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/knowledge")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> knowledge() {
        List<Map<String, Object>> list = Arrays.asList(
                buildKnowledge("k1", "Low-temp protection guide", "published", 3200, "approved", "done"),
                buildKnowledge("k2", "Rice-shrimp disease control", "reviewing", 0, "editing", "review"),
                buildKnowledge("k3", "Agri IoT promotion", "draft", 0, "pending release", "pending"));
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> notifications() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String, String>> list = Arrays.asList(
                buildNotification("n1", "Platform: Nov farmer issues report", formatter.format(LocalDateTime.now())),
                buildNotification("n2", "Reminder: confirm 12/01 onsite session", "today"),
                buildNotification("n3", "Knowledge review: 2 drafts pending", "2 hours ago"));
        return ResponseEntity.ok(ApiResponse.ok(list));
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
