package com.farmporject.backend.trade.controller;

import com.farmporject.backend.trade.model.CartProduct;
import com.farmporject.backend.trade.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trade/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取购物车列表（仅购物车项）
     */
    @GetMapping
    public ResponseEntity<?> getCart(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<CartProduct> cartItems = cartService.getUserCart(userId);
            response.put("flag", true);
            response.put("data", cartItems);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取购物车失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取购物车详情（包含商品信息）
     */
    @GetMapping("/details")
    public ResponseEntity<?> getCartWithProducts(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> cartItems = cartService.getUserCartWithProducts(userId);
            response.put("flag", true);
            response.put("data", cartItems);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取购物车失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Long userId,
                                       @RequestParam Long productId,
                                       @RequestParam Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            CartProduct cartItem = cartService.addToCart(userId, productId, quantity);
            response.put("flag", true);
            response.put("message", "商品已添加到购物车");
            response.put("data", cartItem);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "添加商品到购物车失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam Long userId,
                                            @RequestParam Long productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            cartService.removeFromCart(userId, productId);
            response.put("flag", true);
            response.put("message", "商品已从购物车移除");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "移除购物车商品失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestParam Long userId,
                                           @RequestParam Long productId,
                                           @RequestParam Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (quantity == null || quantity <= 0) {
                response.put("flag", false);
                response.put("message", "数量必须大于0");
                return ResponseEntity.badRequest().body(response);
            }
            CartProduct cartItem = cartService.updateCartItemQuantity(userId, productId, quantity);
            response.put("flag", true);
            response.put("message", "数量已更新");
            response.put("data", cartItem);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "更新数量失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            cartService.clearUserCart(userId);
            response.put("flag", true);
            response.put("message", "购物车已清空");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "清空购物车失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}