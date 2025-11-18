package com.farmporject.backend.expert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * 知识模块-农业知识
 * 路由前缀：/api/knowledge
 * - GET /api/knowledge/{pageNum} 知识列表（分页）
 * - POST /api/knowledge 新增知识
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    /**
     * 知识列表（分页）
     * 示例：GET /api/knowledge/1
     */
    @GetMapping("/{pageNum}")
    public ResponseEntity<?> list(@PathVariable Integer pageNum) {
        // 构建符合前端期望的响应格式
        Map<String, Object> response = new HashMap<>();
        response.put("flag", true);
        
        // 创建测试数据（实际应用中应该从数据库查询）
        List<Map<String, Object>> knowledgeList = new ArrayList<>();
        
        // 测试数据1
        Map<String, Object> knowledge1 = new HashMap<>();
        knowledge1.put("knowledgeId", 1L);
        knowledge1.put("title", "春季小麦种植技术要点");
        knowledge1.put("createTime", "2024-03-15T10:00:00");
        knowledge1.put("updateTime", "2024-03-15T10:00:00");
        knowledgeList.add(knowledge1);
        
        // 测试数据2
        Map<String, Object> knowledge2 = new HashMap<>();
        knowledge2.put("knowledgeId", 2L);
        knowledge2.put("title", "水稻病虫害防治方法");
        knowledge2.put("createTime", "2024-03-14T14:30:00");
        knowledge2.put("updateTime", "2024-03-14T14:30:00");
        knowledgeList.add(knowledge2);
        
        // 测试数据3
        Map<String, Object> knowledge3 = new HashMap<>();
        knowledge3.put("knowledgeId", 3L);
        knowledge3.put("title", "有机肥料使用指南");
        knowledge3.put("createTime", "2024-03-13T09:15:00");
        knowledge3.put("updateTime", "2024-03-13T09:15:00");
        knowledgeList.add(knowledge3);
        
        // 测试数据4
        Map<String, Object> knowledge4 = new HashMap<>();
        knowledge4.put("knowledgeId", 4L);
        knowledge4.put("title", "大棚蔬菜温度控制技巧");
        knowledge4.put("createTime", "2024-03-12T16:45:00");
        knowledge4.put("updateTime", "2024-03-12T16:45:00");
        knowledgeList.add(knowledge4);
        
        // 测试数据5
        Map<String, Object> knowledge5 = new HashMap<>();
        knowledge5.put("knowledgeId", 5L);
        knowledge5.put("title", "果树修剪的最佳时机");
        knowledge5.put("createTime", "2024-03-11T11:20:00");
        knowledge5.put("updateTime", "2024-03-11T11:20:00");
        knowledgeList.add(knowledge5);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", knowledgeList);
        data.put("total", knowledgeList.size());
        
        response.put("data", data);
        
        return ResponseEntity.ok().body(response);
    }

    /**
     * 知识列表（无分页，兼容旧接口）
     * 示例：GET /api/knowledge
     */
    @GetMapping
    public ResponseEntity<?> listAll() {
        Map<String, Object> response = new HashMap<>();
        response.put("flag", true);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", new ArrayList<>());
        data.put("total", 0);
        
        response.put("data", data);
        
        return ResponseEntity.ok().body(response);
    }

    /**
     * 新增知识
     * 示例：POST /api/knowledge
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> knowledge) {
        Map<String, Object> response = new HashMap<>();
        response.put("flag", true);
        response.put("message", "knowledge created");
        return ResponseEntity.status(201).body(response);
    }
}
