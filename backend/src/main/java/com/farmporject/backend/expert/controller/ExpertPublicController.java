package com.farmporject.backend.expert.controller;

import com.farmporject.backend.expert.dto.ExpertDTO;
import com.farmporject.backend.expert.service.ExpertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 面向普通用户的专家信息接口（用于“专家信息”页和统计已入驻专家数量）
 */
@RestController
public class ExpertPublicController {

    private final ExpertService expertService;

    public ExpertPublicController(ExpertService expertService) {
        this.expertService = expertService;
    }

    /**
     * GET /api/experts
     * 分页获取专家列表，并返回总数。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页数量
     * @param keyword  搜索关键字：按姓名 / 职称 / 专业 / 简介模糊匹配
     */
    @GetMapping("/api/experts")
    public ResponseEntity<Map<String, Object>> getExperts(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        try {
            if (pageNum == null || pageNum < 1) {
                pageNum = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 10;
            }

            List<ExpertDTO> all = expertService.getAllExperts();

            // 关键字过滤
            if (keyword != null && !keyword.trim().isEmpty()) {
                final String kw = keyword.trim().toLowerCase();
                all = all.stream()
                        .filter(e -> {
                            String specialties = e.getSpecialties() != null
                                    ? String.join("、", e.getSpecialties())
                                    : "";
                            return (e.getName() != null && e.getName().toLowerCase().contains(kw))
                                    || (e.getTitle() != null && e.getTitle().toLowerCase().contains(kw))
                                    || (e.getDescription() != null && e.getDescription().toLowerCase().contains(kw))
                                    || specialties.toLowerCase().contains(kw);
                        })
                        .collect(Collectors.toList());
            }

            int total = all.size();
            int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
            int toIndex = Math.min(total, fromIndex + pageSize);

            List<ExpertDTO> pageList;
            if (fromIndex >= total) {
                pageList = Collections.emptyList();
            } else {
                pageList = all.subList(fromIndex, toIndex);
            }

            // 映射成前端期望结构
            List<Map<String, Object>> list = new ArrayList<>();
            for (ExpertDTO dto : pageList) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", dto.getId());
                item.put("realName", dto.getName());
                item.put("position", dto.getTitle());

                if (dto.getSpecialties() != null && !dto.getSpecialties().isEmpty()) {
                    item.put("profession", String.join("、", dto.getSpecialties()));
                } else {
                    item.put("profession", null);
                }

                item.put("belong", dto.getDescription());
                item.put("phone", dto.getContactInfo());
                item.put("isAvailable", dto.getIsAvailable());
                item.put("userName", dto.getUsername());
                item.put("avatar", dto.getAvatar());

                list.add(item);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", total);

            Map<String, Object> resp = new HashMap<>();
            resp.put("flag", true);
            resp.put("data", data);

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("flag", false);
            resp.put("message", "获取专家列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}


