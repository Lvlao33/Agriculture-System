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
        data.put("todayAppointments", 4);
        data.put("appointmentSummary", "视频 2 个 现场 1");
        data.put("publishedKnowledge", 7);
        data.put("knowledgeTrend", "阅读量 +18%");
        data.put("expertScore", 4.9);
        data.put("reviewCount", 126);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> questions() {
        List<Map<String, Object>> list = Arrays.asList(
                buildQuestion("q1", "紧急", "番茄叶片发黄原因未解决", "山东农户", "09:12", "待回复", "pending"),
                buildQuestion("q2", "紧急", "黄瓜卷叶是否缺钙？", "河北种植户", "08:40", "审核中", "review"),
                buildQuestion("q3", "普通", "水稻抽穗期施肥要点", "家庭农场主", "昨天 21:05", "已回复", "done"));
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> appointments() {
        List<Map<String, Object>> list = Arrays.asList(
                buildAppointment("a1", "10:30", "视频咨询", "平度大棚合作社", "果树管理", "等待开始", "review"),
                buildAppointment("a2", "14:00", "现场指导", "平度大棚合作社", "病虫害防治", "进行中", "pending"),
                buildAppointment("a3", "19:30", "群直播课", "平台入驻农户", "秋冬管理", "待确认", "done"));
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/knowledge")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> knowledge() {
        List<Map<String, Object>> list = Arrays.asList(
                buildKnowledge("k1", "小麦赤霉病防治指南", "已发布", 3200, "通过审核", "done"),
                buildKnowledge("k2", "温室蔬菜湿度管理技巧", "待审核", 0, "待审核", "review"),
                buildKnowledge("k3", "农业技术推广", "草稿", 0, "待发布", "pending"));
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> notifications() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String, String>> list = Arrays.asList(
                buildNotification("n1", "平台通知：11 月农户问题报告", formatter.format(LocalDateTime.now())),
                buildNotification("n2", "预约提醒：请确认 12/01 现场指导", "今天"),
                buildNotification("n3", "知识审核：有 2 篇内容待编辑", "2 小时前"));
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
