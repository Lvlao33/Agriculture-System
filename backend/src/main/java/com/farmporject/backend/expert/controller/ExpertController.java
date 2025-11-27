package com.farmporject.backend.expert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/expert")
public class ExpertController {

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile() {
        Map<String, Object> m = new HashMap<>();
        m.put("name", "示例专家");
        m.put("title", "农业技术专家");
        m.put("bio", "拥有多年农业种植经验与病虫害防治经验。");
        m.put("rating", 4.8);
        return ResponseEntity.ok(m);
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, Object> payload) {
        // TODO: persist changes; currently echo back
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("payload", payload);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<Map<String, Object>>> getAppointments() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(appointment("2025-11-28 10:00", "用户A", "春季施肥技巧咨询", 1L));
        list.add(appointment("2025-11-29 14:00", "用户B", "果树病虫害防治", 2L));
        return ResponseEntity.ok(list);
    }

    @PostMapping("/appointments/{id}/accept")
    public ResponseEntity<Map<String, Object>> acceptAppointment(@PathVariable Long id) {
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("id", id);
        r.put("status", "accepted");
        return ResponseEntity.ok(r);
    }

    @PostMapping("/appointments/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectAppointment(@PathVariable Long id) {
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("id", id);
        r.put("status", "rejected");
        return ResponseEntity.ok(r);
    }

    @GetMapping("/knowledges")
    public ResponseEntity<List<Map<String, Object>>> getKnowledges() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(knowledge(1L, "温室蔬菜种植要点", "蔬菜病害"));
        list.add(knowledge(2L, "果树花期管理指导", "果树管理"));
        return ResponseEntity.ok(list);
    }

    @PostMapping("/knowledges")
    public ResponseEntity<Map<String, Object>> publishKnowledge(@RequestBody Map<String, Object> payload) {
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("createdId", 12345);
        r.put("payload", payload);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Map<String, Object>>> getMessages() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> m1 = new HashMap<>();
        m1.put("title", "新的咨询预约");
        m1.put("time", "2025-11-26 09:00");
        list.add(m1);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/earnings")
    public ResponseEntity<Map<String, Object>> getEarnings() {
        Map<String, Object> m = new HashMap<>();
        m.put("month", 1200.50);
        m.put("total", 15230.75);
        return ResponseEntity.ok(m);
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        Map<String, Object> m = new HashMap<>();
        m.put("visits", Arrays.asList(10, 15, 12, 20, 18));
        m.put("dates", Arrays.asList("2025-11-22","2025-11-23","2025-11-24","2025-11-25","2025-11-26"));
        return ResponseEntity.ok(m);
    }

    private Map<String, Object> appointment(String date, String user, String topic, Long id) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("date", date);
        m.put("userName", user);
        m.put("topic", topic);
        m.put("status", "pending");
        return m;
    }

    private Map<String, Object> knowledge(Long id, String title, String category) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("title", title);
        m.put("category", category);
        m.put("summary", title + " 的摘要");
        return m;
    }
}
