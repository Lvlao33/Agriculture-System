package com.farmporject.backend.trade.controller;

import com.farmporject.backend.trade.model.Demand;
import com.farmporject.backend.trade.service.DemandService;
import com.farmporject.backend.security.UserContext;
import com.farmporject.backend.config.JwtConfig;
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
    
    @Autowired
    private JwtConfig jwtConfig;
    
    /**
     * 从token中解析用户ID（支持JWT和旧格式token）
     */
    private Long parseUserId(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        // 优先使用UserContext（如果JWT过滤器已填充）
        if (UserContext.isAuthenticated()) {
            return UserContext.getCurrentUserId();
        }
        
        String actualToken = token;
        if (token.startsWith("Bearer ") || token.startsWith("bearer ")) {
            actualToken = token.substring(7).trim();
        }
        
        // 优先尝试解析 JWT token
        try {
            if (jwtConfig.validateToken(actualToken)) {
                Long userId = jwtConfig.getUserIdFromToken(actualToken);
                if (userId != null) {
                    return userId;
                }
            }
        } catch (Exception e) {
            // JWT解析失败，继续尝试旧格式
        }
        
        // 兼容旧格式 token: tk_<userId>_<username>
        if (actualToken.startsWith("tk_")) {
            String[] parts = actualToken.split("_");
            if (parts.length >= 2) {
                try {
                    return Long.parseLong(parts[1]);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        
        return null;
    }

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

    /** 更新采购需求 */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id, 
            @RequestBody Demand demand,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 解析 userId（支持JWT和旧格式token）
            Long userId = parseUserId(token);
            
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }
            
            // 检查需求是否存在
            var existingDemandOpt = demandService.getDemandById(id);
            if (!existingDemandOpt.isPresent()) {
                response.put("flag", false);
                response.put("message", "需求不存在");
                return ResponseEntity.ok().body(response);
            }

            Demand existingDemand = existingDemandOpt.get();
            
            // 检查权限：只有需求创建者可以更新
            if (!existingDemand.getUserId().equals(userId)) {
                response.put("flag", false);
                response.put("message", "无权修改此需求");
                return ResponseEntity.status(403).body(response);
            }
            
            // 设置 userId（确保更新时使用正确的 userId）
            demand.setUserId(userId);

            // 更新需求
            Demand updatedDemand = demandService.updateDemand(id, demand);

            response.put("flag", true);
            response.put("message", "需求更新成功");
            response.put("data", updatedDemand);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "更新需求失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /** 删除采购需求 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 解析 userId（支持JWT和旧格式token）
            Long userId = parseUserId(token);
            
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }
            
            // 检查需求是否存在
            var existingDemandOpt = demandService.getDemandById(id);
            if (!existingDemandOpt.isPresent()) {
                response.put("flag", false);
                response.put("message", "需求不存在");
                return ResponseEntity.ok().body(response);
            }
            
            Demand existingDemand = existingDemandOpt.get();
            
            // 检查权限：只有需求创建者可以删除
            if (!existingDemand.getUserId().equals(userId)) {
                response.put("flag", false);
                response.put("message", "无权删除此需求");
                return ResponseEntity.status(403).body(response);
            }
            
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