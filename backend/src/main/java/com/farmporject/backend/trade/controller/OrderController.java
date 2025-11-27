package com.farmporject.backend.trade.controller;

import com.farmporject.backend.trade.model.Order;
import com.farmporject.backend.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trade/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Order> orders;

            if (userId != null && status != null) {
                orders = orderService.getUserOrdersByStatus(userId, status);
            } else if (userId != null) {
                orders = orderService.getUserOrders(userId);
            } else if (status != null) {
                orders = orderService.getOrdersByStatus(status);
            } else {
                orders = orderService.getAllOrders();
            }

            response.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", orders);
            data.put("total", orders.size());

            response.put("data", data);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取订单列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Order order) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (order.getUserId() == null) {
                response.put("flag", false);
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                response.put("flag", false);
                response.put("message", "订单金额必须大于0");
                return ResponseEntity.badRequest().body(response);
            }

            Order createdOrder = orderService.createOrder(order);

            response.put("flag", true);
            response.put("message", "订单创建成功");
            response.put("data", createdOrder);
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "创建订单失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            var order = orderService.getOrderById(id);
            if (order.isPresent()) {
                response.put("flag", true);
                response.put("data", order.get());
                return ResponseEntity.ok().body(response);
            } else {
                response.put("flag", false);
                response.put("message", "订单不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取订单详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}