package com.farmporject.backend.trade.controller;

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
 */
@RestController
@RequestMapping("/cart")
public class CartCompatController {

    @Autowired
    private CartService cartService;

    private Long parseUserId(String token) {
        if (token != null && token.startsWith("tk_")) {
            String[] parts = token.split("_");
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



