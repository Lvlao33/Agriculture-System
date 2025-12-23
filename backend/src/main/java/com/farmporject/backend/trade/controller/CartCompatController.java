package com.farmporject.backend.trade.controller;

import com.farmporject.backend.config.JwtConfig;
import com.farmporject.backend.trade.model.CartProduct;
import com.farmporject.backend.trade.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 兼容前端旧版购物车接口（/cart/...），基于 token 提取 userId
 * 支持两种token格式：
 * 1. JWT token（新格式，由 AuthController 返回）
 * 2. 旧格式 token（tk_<userId>_<username>）
 */
@RestController
@RequestMapping("/cart")
public class CartCompatController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 从token中解析用户ID
     * 支持两种格式：
     * 1. JWT token（优先尝试）
     * 2. 旧格式：tk_<userId>_<username>
     */
    private Long parseUserId(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        // 处理 "Bearer <token>" 格式
        String actualToken = token;
        if (token.startsWith("Bearer ")) {
            actualToken = token.substring(7);
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
            System.out.println("JWT解析失败，尝试旧格式token: " + e.getMessage());
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

    /** 显示购物车列表 */
    @GetMapping("/show")
    public ResponseEntity<?> showCart(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> res = new HashMap<>();
        // 检查token是否过期（针对JWT token）
        if (token != null && !token.isEmpty()) {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            // 如果是JWT格式（不以tk_开头），检查是否过期
            if (!actualToken.startsWith("tk_")) {
                if (jwtConfig.isTokenExpired(actualToken)) {
                    res.put("flag", false);
                    res.put("message", "token已过期，请重新登录");
                    res.put("data", new ArrayList<>());
                    return ResponseEntity.status(401).body(res);
                }
            }
        }
        
        Long userId = parseUserId(token);
        if (userId == null) {
            res.put("flag", false);
            res.put("message", "请先登录");
            res.put("data", new ArrayList<>());
            return ResponseEntity.status(401).body(res);
        }
        // 返回包含商品详情的购物车数据
        List<Map<String, Object>> list = cartService.getUserCartWithProducts(userId);
        res.put("flag", true);
        res.put("data", list);
        return ResponseEntity.ok(res);
    }

    /** 加入购物车：/cart/add/{productId} */
    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToCart(@PathVariable Long productId,
                                       @RequestHeader(value = "Authorization", required = false) String token,
                                       @RequestParam(value = "quantity", required = false, defaultValue = "1") Integer quantity) {
        Map<String, Object> res = new HashMap<>();
        try {
            // 检查token是否过期（针对JWT token）
            if (token != null && !token.isEmpty()) {
                String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
                // 如果是JWT格式（不以tk_开头），检查是否过期
                if (!actualToken.startsWith("tk_")) {
                    if (jwtConfig.isTokenExpired(actualToken)) {
                        res.put("flag", false);
                        res.put("message", "token已过期，请重新登录");
                        return ResponseEntity.status(401).body(res);
                    }
                }
            }
            
            Long userId = parseUserId(token);
            if (userId == null) {
                res.put("flag", false);
                res.put("message", "请先登录");
                return ResponseEntity.status(401).body(res);
            }
            if (quantity == null || quantity <= 0) quantity = 1;
            
            // 验证productId是否有效
            if (productId == null || productId <= 0) {
                res.put("flag", false);
                res.put("message", "商品ID无效");
                return ResponseEntity.badRequest().body(res);
            }
            
            CartProduct item = cartService.addToCart(userId, productId, quantity);
            res.put("flag", true);
            res.put("message", "加入购物车成功");
            res.put("data", item);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("flag", false);
            res.put("message", "加入购物车失败: " + e.getMessage());
            e.printStackTrace(); // 打印堆栈信息便于调试
            return ResponseEntity.badRequest().body(res);
        }
    }

    /** 删除购物车商品：/cart/delete/{productId} */
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteFromCart(@PathVariable Long productId,
                                            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> res = new HashMap<>();
        // 检查token是否过期（针对JWT token）
        if (token != null && !token.isEmpty()) {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            if (!actualToken.startsWith("tk_")) {
                if (jwtConfig.isTokenExpired(actualToken)) {
                    res.put("flag", false);
                    res.put("message", "token已过期，请重新登录");
                    return ResponseEntity.status(401).body(res);
                }
            }
        }
        
        Long userId = parseUserId(token);
        if (userId == null) {
            res.put("flag", false);
            res.put("message", "请先登录");
            return ResponseEntity.status(401).body(res);
        }
        cartService.removeFromCart(userId, productId);
        res.put("flag", true);
        res.put("message", "删除成功");
        return ResponseEntity.ok(res);
    }

    /** 更新数量：/cart/update/{productId}/{count} */
    @PutMapping("/update/{productId}/{count}")
    public ResponseEntity<?> updateCount(@PathVariable Long productId,
                                         @PathVariable Integer count,
                                         @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> res = new HashMap<>();
        // 检查token是否过期（针对JWT token）
        if (token != null && !token.isEmpty()) {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            if (!actualToken.startsWith("tk_")) {
                if (jwtConfig.isTokenExpired(actualToken)) {
                    res.put("flag", false);
                    res.put("message", "token已过期，请重新登录");
                    return ResponseEntity.status(401).body(res);
                }
            }
        }
        
        Long userId = parseUserId(token);
        if (userId == null) {
            res.put("flag", false);
            res.put("message", "请先登录");
            return ResponseEntity.status(401).body(res);
        }
        if (count == null || count <= 0) {
            res.put("flag", false);
            res.put("message", "数量必须大于0");
            return ResponseEntity.badRequest().body(res);
        }
        CartProduct item = cartService.updateCartItemQuantity(userId, productId, count);
        res.put("flag", true);
        res.put("message", "数量已更新");
        res.put("data", item);
        return ResponseEntity.ok(res);
    }
}



