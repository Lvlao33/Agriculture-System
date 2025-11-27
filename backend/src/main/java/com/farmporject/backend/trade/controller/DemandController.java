package com.farmporject.backend.trade.controller;

import com.farmporject.backend.trade.model.Demand;
import com.farmporject.backend.trade.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trade/demands")
public class DemandController {

    @Autowired
    private DemandService demandService;

    @GetMapping
    public ResponseEntity<?> list() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Demand> demands = demandService.getAllDemands();
            response.put("flag", true);
            response.put("data", demands);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取需求列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Demand demand) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (demand.getTitle() == null || demand.getTitle().trim().isEmpty()) {
                response.put("flag", false);
                response.put("message", "需求标题不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (demand.getUserId() == null) {
                response.put("flag", false);
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            Demand createdDemand = demandService.createDemand(demand);

            response.put("flag", true);
            response.put("message", "需求发布成功");
            response.put("data", createdDemand);
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "发布需求失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            var demand = demandService.getDemandById(id);
            if (demand.isPresent()) {
                response.put("flag", true);
                response.put("data", demand.get());
                return ResponseEntity.ok().body(response);
            } else {
                response.put("flag", false);
                response.put("message", "需求不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取需求详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}