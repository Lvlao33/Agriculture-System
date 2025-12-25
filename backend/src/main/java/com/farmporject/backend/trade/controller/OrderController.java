package com.farmporject.backend.trade.controller;

import com.farmporject.backend.config.JwtConfig;
import com.farmporject.backend.trade.model.Order;
import com.farmporject.backend.trade.service.OrderService;
import com.farmporject.backend.trade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/trade/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    /**
     * 从token中解析用户ID
     */
    private Long parseUserId(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
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
    public ResponseEntity<?> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Map<String, Object> response = new HashMap<>();
        try {
            // 如果未提供userId，尝试从token中提取
            if (userId == null) {
                userId = parseUserId(token);
            }

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

            // 为每个订单添加订单项信息
            List<Map<String, Object>> orderList = new ArrayList<>();
            for (Order order : orders) {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("orderNumber", order.getOrderNumber());
                orderMap.put("totalAmount", order.getTotalAmount());
                orderMap.put("status", order.getStatus());
                orderMap.put("createTime", order.getCreateTime());
                orderMap.put("userId", order.getUserId());
                orderMap.put("sellerId", order.getSellerId());
                orderMap.put("shippingAddress", order.getShippingAddress());
                orderMap.put("receiverName", order.getReceiverName());
                orderMap.put("receiverPhone", order.getReceiverPhone());
                orderMap.put("paymentMethod", order.getPaymentMethod());
                orderMap.put("paymentStatus", order.getPaymentStatus());
                
                // 添加订单项，并为每个订单项添加商品图片信息
                List<com.farmporject.backend.trade.model.OrderItem> orderItems = orderService.getOrderItems(order.getId());
                List<Map<String, Object>> orderItemsWithImage = new ArrayList<>();
                for (com.farmporject.backend.trade.model.OrderItem item : orderItems) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("orderId", item.getOrderId());
                    itemMap.put("productId", item.getProductId());
                    itemMap.put("productName", item.getProductName());
                    itemMap.put("productPrice", item.getProductPrice());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("subtotal", item.getSubtotal());
                    // 查询商品图片
                    try {
                        var productOpt = productService.getProductById(item.getProductId());
                        if (productOpt.isPresent()) {
                            itemMap.put("imageUrl", productOpt.get().getImageUrl());
                        }
                    } catch (Exception e) {
                        // 如果查询商品失败，忽略
                    }
                    orderItemsWithImage.add(itemMap);
                }
                orderMap.put("orderItems", orderItemsWithImage);
                
                orderList.add(orderMap);
            }

            response.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", orderList);
            data.put("total", orderList.size());

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
                Order orderObj = order.get();
                // 构建包含订单项的订单数据
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", orderObj.getId());
                orderMap.put("orderId", orderObj.getId());
                orderMap.put("order_id", orderObj.getId());
                orderMap.put("orderNumber", orderObj.getOrderNumber());
                orderMap.put("totalAmount", orderObj.getTotalAmount());
                orderMap.put("total_amount", orderObj.getTotalAmount());
                orderMap.put("status", orderObj.getStatus());
                orderMap.put("createTime", orderObj.getCreateTime());
                orderMap.put("create_time", orderObj.getCreateTime());
                orderMap.put("userId", orderObj.getUserId());
                orderMap.put("sellerId", orderObj.getSellerId());
                orderMap.put("shippingAddress", orderObj.getShippingAddress());
                orderMap.put("receiverName", orderObj.getReceiverName());
                orderMap.put("receiverPhone", orderObj.getReceiverPhone());
                orderMap.put("paymentMethod", orderObj.getPaymentMethod());
                orderMap.put("paymentStatus", orderObj.getPaymentStatus());
                
                // 添加订单项（重要：包含 productId 和商品图片）
                List<com.farmporject.backend.trade.model.OrderItem> orderItems = orderService.getOrderItems(orderObj.getId());
                List<Map<String, Object>> orderItemsWithImage = new ArrayList<>();
                for (com.farmporject.backend.trade.model.OrderItem item : orderItems) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("orderId", item.getOrderId());
                    itemMap.put("productId", item.getProductId());
                    itemMap.put("productName", item.getProductName());
                    itemMap.put("productPrice", item.getProductPrice());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("subtotal", item.getSubtotal());
                    // 查询商品图片
                    try {
                        var productOpt = productService.getProductById(item.getProductId());
                        if (productOpt.isPresent()) {
                            itemMap.put("imageUrl", productOpt.get().getImageUrl());
                        }
                    } catch (Exception e) {
                        // 如果查询商品失败，忽略
                    }
                    orderItemsWithImage.add(itemMap);
                }
                orderMap.put("orderItems", orderItemsWithImage);
                
                response.put("flag", true);
                response.put("data", orderMap);
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

    /**
     * 从购物车创建订单（待付款状态）- 每种商品创建一个独立订单
     * POST /api/trade/orders/checkout
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> request,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = parseUserId(token);
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }

            @SuppressWarnings("unchecked")
            List<Number> productIdsRaw = (List<Number>) request.get("productIds");
            if (productIdsRaw == null || productIdsRaw.isEmpty()) {
                response.put("flag", false);
                response.put("message", "请选择要购买的商品");
                return ResponseEntity.badRequest().body(response);
            }

            List<Long> productIds = new ArrayList<>();
            for (Number id : productIdsRaw) {
                productIds.add(id.longValue());
            }

            String shippingAddress = (String) request.getOrDefault("shippingAddress", "");
            String receiverName = (String) request.getOrDefault("receiverName", "");
            String receiverPhone = (String) request.getOrDefault("receiverPhone", "");

            // 为每种商品创建独立订单
            List<Order> orders = orderService.createOrdersByProductType(userId, productIds, shippingAddress, receiverName, receiverPhone);

            response.put("flag", true);
            response.put("message", "成功创建 " + orders.size() + " 个待付款订单");
            response.put("data", orders);
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "创建订单失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 立即购买：直接从商品创建订单（不经过购物车）
     * POST /api/trade/orders/buy-now
     */
    @PostMapping("/buy-now")
    public ResponseEntity<?> buyNow(@RequestBody Map<String, Object> request,
                                    @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = parseUserId(token);
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }

            // 获取商品ID
            Object productIdObj = request.get("productId");
            if (productIdObj == null) {
                response.put("flag", false);
                response.put("message", "商品ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            Long productId = null;
            if (productIdObj instanceof Number) {
                productId = ((Number) productIdObj).longValue();
            } else if (productIdObj instanceof String) {
                try {
                    productId = Long.parseLong((String) productIdObj);
                } catch (NumberFormatException e) {
                    response.put("flag", false);
                    response.put("message", "商品ID格式错误");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // 获取购买数量（默认为1）
            Integer quantity = 1;
            Object quantityObj = request.get("quantity");
            if (quantityObj instanceof Number) {
                quantity = ((Number) quantityObj).intValue();
            } else if (quantityObj instanceof String) {
                try {
                    quantity = Integer.parseInt((String) quantityObj);
                } catch (NumberFormatException e) {
                    // 使用默认值
                }
            }
            if (quantity == null || quantity <= 0) {
                quantity = 1;
            }

            String shippingAddress = (String) request.getOrDefault("shippingAddress", "");
            String receiverName = (String) request.getOrDefault("receiverName", "");
            String receiverPhone = (String) request.getOrDefault("receiverPhone", "");

            // 直接从商品创建订单
            Order order = orderService.createOrderDirectly(userId, productId, quantity, 
                                                          shippingAddress, receiverName, receiverPhone);

            response.put("flag", true);
            response.put("message", "订单创建成功，请前往订单管理付款");
            response.put("data", order);
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "创建订单失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 订单付款：将待付款订单更新为待发货状态
     * PUT /api/trade/orders/{id}/pay
     */
    @PutMapping("/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long id,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = parseUserId(token);
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }

            Order order = orderService.payOrder(id);
            
            // 验证订单是否属于当前用户（买家）
            if (!order.getUserId().equals(userId)) {
                response.put("flag", false);
                response.put("message", "无权操作此订单");
                return ResponseEntity.status(403).body(response);
            }

            response.put("flag", true);
            response.put("message", "付款成功，订单已进入待发货状态");
            response.put("data", order);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "付款失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 确认发货：将待发货订单更新为待收货状态
     * PUT /api/trade/orders/{id}/ship
     */
    @PutMapping("/{id}/ship")
    public ResponseEntity<?> shipOrder(@PathVariable Long id,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = parseUserId(token);
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }

            Order order = orderService.shipOrder(id);
            
            // 验证订单是否属于当前用户（卖家）
            if (!order.getSellerId().equals(userId)) {
                response.put("flag", false);
                response.put("message", "无权操作此订单");
                return ResponseEntity.status(403).body(response);
            }

            response.put("flag", true);
            response.put("message", "发货成功，订单已进入待收货状态");
            response.put("data", order);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "发货失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 确认收货：将订单状态更新为待评价（pending_review）
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmReceipt(@PathVariable Long id,
                                            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = parseUserId(token);
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }

            Order order = orderService.confirmReceipt(id);
            
            // 验证订单是否属于当前用户（买家）
            if (!order.getUserId().equals(userId)) {
                response.put("flag", false);
                response.put("message", "无权操作此订单");
                return ResponseEntity.status(403).body(response);
            }

            response.put("flag", true);
            response.put("message", "确认收货成功，订单已进入待评价");
            response.put("data", order);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "确认收货失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 完成评价：将待评价订单更新为退款/售后状态
     * PUT /api/trade/orders/{id}/complete-review
     */
    @PutMapping("/{id}/complete-review")
    public ResponseEntity<?> completeReview(@PathVariable Long id,
                                           @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = parseUserId(token);
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }

            Order order = orderService.completeReview(id);
            
            // 验证订单是否属于当前用户（买家）
            if (!order.getUserId().equals(userId)) {
                response.put("flag", false);
                response.put("message", "无权操作此订单");
                return ResponseEntity.status(403).body(response);
            }

            response.put("flag", true);
            response.put("message", "评价完成，订单已进入退款/售后");
            response.put("data", order);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "完成评价失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除订单
     * DELETE /api/trade/orders/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id,
                                        @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = parseUserId(token);
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }

            // 先获取订单信息，验证权限和状态
            var orderOpt = orderService.getOrderById(id);
            if (orderOpt.isEmpty()) {
                response.put("flag", false);
                response.put("message", "订单不存在");
                return ResponseEntity.status(404).body(response);
            }

            Order order = orderOpt.get();
            
            // 验证订单是否属于当前用户
            if (!order.getUserId().equals(userId)) {
                response.put("flag", false);
                response.put("message", "无权删除此订单");
                return ResponseEntity.status(403).body(response);
            }

            // 检查订单状态：只有待付款、退款/售后状态的订单可以删除
            // 已付款、待收货、待评价的订单不能删除（保护用户权益）
            String status = order.getStatus();
            if (!"pending_payment".equals(status) && !"refund_after_sale".equals(status)) {
                response.put("flag", false);
                response.put("message", "当前订单状态不允许删除，只能删除待付款或退款/售后状态的订单");
                return ResponseEntity.badRequest().body(response);
            }

            // 执行删除
            orderService.deleteOrder(id);

            response.put("flag", true);
            response.put("message", "订单删除成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "删除订单失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}