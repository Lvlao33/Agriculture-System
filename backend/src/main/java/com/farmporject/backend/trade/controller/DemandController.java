package com.farmporject.backend.trade.controller;

import com.farmporject.backend.trade.model.Demand;
import com.farmporject.backend.trade.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易模块-采购需求
 * 路由前缀：/api/trade/demands
 * - GET /api/trade/demands 采购需求列表
 * - POST /api/trade/demands 发布采购需求
 */
@RestController
@RequestMapping("/api/trade/demands")
public class DemandController {

    @Autowired
    private DemandService demandService;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) Long userId,
                                  @RequestParam(required = false) String keyword) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Demand> demands;
            if (userId != null) {
                demands = demandService.getUserDemands(userId);
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                demands = demandService.searchDemands(keyword.trim());
            } else {
                demands = demandService.getAllDemands();
            }
            response.put("flag", true);
            response.put("data", demands);
            response.put("total", demands.size());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取需求列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /** 发布采购需求 */
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

    /** 删除采购需求 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            demandService.deleteDemand(id);
            response.put("flag", true);
            response.put("message", "需求删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "删除需求失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}