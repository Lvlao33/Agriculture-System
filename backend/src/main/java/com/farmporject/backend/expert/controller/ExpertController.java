package com.farmporject.backend.expert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmporject.backend.expert.dto.ExpertDTO;
import com.farmporject.backend.expert.service.ExpertService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 专家相关接口
 * 统一前缀: /api/expert
 */
@RestController
@RequestMapping("/api/expert")
public class ExpertController {

    private final ExpertService expertService;

    public ExpertController(ExpertService expertService) {
        this.expertService = expertService;
    }

    // 获得专家介绍详情（用于后台管理或个人中心）
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(expertService.getAllExperts());
    }

    // 更新专家介绍详情
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, Object> payload) {
        // 暂不实现完整功能，当前只返回成功响应
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("payload", payload);
        return ResponseEntity.ok(resp);
    }

    /**
     * 获取专家列表（支持分页和搜索）
     * 核心接口：供 ExpertGuide.vue 使用
     */
    @GetMapping("/infoList")
    public ResponseEntity<Map<String, Object>> getExperts(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        Map<String, Object> resp = new HashMap<>();
        try {
            if (pageNum < 1) pageNum = 1;
            
            List<ExpertDTO> all = expertService.getAllExperts();

            // 关键字过滤
            if (keyword != null && !keyword.trim().isEmpty()) {
                final String kw = keyword.trim().toLowerCase();
                all = all.stream().filter(e -> 
                    (e.getName() != null && e.getName().toLowerCase().contains(kw)) ||
                    (e.getTitle() != null && e.getTitle().toLowerCase().contains(kw)) ||
                    (e.getDescription() != null && e.getDescription().toLowerCase().contains(kw))
                ).collect(Collectors.toList());
            }

            int total = all.size();
            int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
            int toIndex = Math.min(total, fromIndex + pageSize);
            
            List<ExpertDTO> pageList = (fromIndex >= total) ? 
                Collections.emptyList() : all.subList(fromIndex, toIndex);

            // 映射为前端需要的字段名
            List<Map<String, Object>> list = new ArrayList<>();
            for (ExpertDTO dto : pageList) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", dto.getId());
                item.put("realName", dto.getName());
                item.put("position", dto.getTitle());
                item.put("profession", dto.getSpecialties() != null ? String.join("、", dto.getSpecialties()) : "");
                item.put("belong", dto.getDescription());
                item.put("phone", dto.getContactInfo());
                item.put("userName", dto.getUsername());
                item.put("avatar", dto.getAvatar());
                list.add(item);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", total);

            resp.put("flag", true);
            resp.put("data", data);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "获取专家列表失败");
            return ResponseEntity.status(500).body(resp);
        }
    }

    // ==========================================
    // 下方为 Dashboard 相关的 Mock (假数据) 接口
    // 仅用于展示效果，后续需替换为真实业务逻辑
    // ==========================================

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
        m.put("dates", Arrays.asList("2025-11-22", "2025-11-23", "2025-11-24", "2025-11-25", "2025-11-26"));
        return ResponseEntity.ok(m);
    }

    // 接受预约 (Mock)
    @PostMapping("/appointments/{id}/accept")
    public ResponseEntity<Map<String, Object>> acceptAppointment(@PathVariable Long id) {
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("id", id);
        r.put("status", "accepted");
        return ResponseEntity.ok(r);
    }

    // 拒绝预约 (Mock)
    @PostMapping("/appointments/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectAppointment(@PathVariable Long id) {
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("id", id);
        r.put("status", "rejected");
        return ResponseEntity.ok(r);
    }
}