package com.farmporject.backend.trade.controller;

import com.farmporject.backend.config.JwtConfig;
import com.farmporject.backend.trade.model.Product;
import com.farmporject.backend.trade.model.Order;
import com.farmporject.backend.trade.service.ProductService;
import com.farmporject.backend.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 兼容旧前端使用的 /order 接口
 * - POST /order - 创建商品（type=goods）
 * - GET /order/goods/{page} - 分页获取商品列表
 * - GET /order/searchGoodsByKeys/{keys}/{page} - 关键词搜索商品
 */
@RestController
@RequestMapping("/order")
public class OrderCompatController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private OrderService orderService;

    /**
     * 从token中解析用户ID（支持JWT和旧格式token）
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

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("收到发布商品请求，token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
            System.out.println("请求体: " + requestBody);
            
            String type = (String) requestBody.get("type");
            System.out.println("商品类型: " + type);

            // 仅处理 goods 类型，其余可按需扩展
            if (!"goods".equals(type)) {
                response.put("flag", false);
                response.put("message", "暂不支持的类型: " + type);
                return ResponseEntity.badRequest().body(response);
            }

            // 解析 sellerId（支持JWT和旧格式token）
            Long sellerId = parseUserId(token);
            System.out.println("解析到的sellerId: " + sellerId);
            if (sellerId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }

            // 字段映射
            String name = (String) requestBody.getOrDefault("title", requestBody.get("name"));
            String description = (String) requestBody.getOrDefault("content", requestBody.get("description"));
            String imageUrl = (String) requestBody.getOrDefault("picture", requestBody.get("imageUrl"));
            String category = (String) requestBody.get("category");

            System.out.println("商品名称: " + name);
            System.out.println("商品描述: " + description);
            System.out.println("商品图片: " + imageUrl);
            System.out.println("商品分类: " + category);

            if (name == null || name.trim().isEmpty()) {
                response.put("flag", false);
                response.put("message", "商品名称不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            Object priceObj = requestBody.get("price");
            BigDecimal price = null;
            if (priceObj instanceof Number) {
                price = BigDecimal.valueOf(((Number) priceObj).doubleValue());
            } else if (priceObj instanceof String) {
                try {
                    price = new BigDecimal((String) priceObj);
                } catch (NumberFormatException e) {
                    System.err.println("价格格式错误: " + priceObj);
                    response.put("flag", false);
                    response.put("message", "价格格式错误: " + priceObj);
                    return ResponseEntity.badRequest().body(response);
                }
            }
            System.out.println("商品价格: " + price);
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                response.put("flag", false);
                response.put("message", "商品价格必须大于0");
                return ResponseEntity.badRequest().body(response);
            }

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setImageUrl(imageUrl);
            product.setCategory(category);
            product.setSellerId(sellerId);
            product.setStock(0);
            product.setIsAvailable(true);

            System.out.println("准备保存商品到数据库...");
            try {
                Product createdProduct = productService.createProduct(product);
                System.out.println("商品保存成功，ID: " + createdProduct.getId());
                
                response.put("flag", true);
                response.put("message", "发布成功");
                response.put("data", createdProduct);
                return ResponseEntity.status(201).body(response);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                System.err.println("数据库约束违反: " + e.getMessage());
                String errorMsg = "发布失败";
                if (e.getMessage() != null) {
                    if (e.getMessage().contains("foreign key") || e.getMessage().contains("seller_id")) {
                        errorMsg = "用户不存在，请先注册或登录";
                    } else if (e.getMessage().contains("Duplicate entry")) {
                        errorMsg = "商品已存在，请勿重复发布";
                    } else {
                        errorMsg = "数据保存失败: " + e.getMessage();
                    }
                }
                response.put("flag", false);
                response.put("message", errorMsg);
                return ResponseEntity.badRequest().body(response);
            } catch (org.springframework.dao.DataAccessException e) {
                System.err.println("数据库访问异常: " + e.getMessage());
                response.put("flag", false);
                response.put("message", "数据库操作失败: " + e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            System.err.println("发布商品异常: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            String errorMessage = "发布失败: " + e.getMessage();
            if (e.getCause() != null) {
                errorMessage += " (" + e.getCause().getMessage() + ")";
            }
            response.put("flag", false);
            response.put("message", errorMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /order/goods/{page} - 分页获取商品列表
     */
    @GetMapping("/goods/{page}")
    public ResponseEntity<?> listLegacyGoods(@PathVariable("page") int page) {
        return listWithKeyword(null, page);
    }

    /**
     * GET /order/searchGoodsByKeys/{keys}/{page} - 关键词搜索商品
     */
    @GetMapping("/searchGoodsByKeys/{keys}/{page}")
    public ResponseEntity<?> searchLegacyGoods(@PathVariable("keys") String keys,
                                               @PathVariable("page") int page) {
        return listWithKeyword(keys, page);
    }

    /**
     * 内部方法：根据关键词分页查询商品
     */
    private ResponseEntity<?> listWithKeyword(String keyword, int page) {
        Map<String, Object> resp = new HashMap<>();
        try {
            int pageIndex = Math.max(page - 1, 0);
            int pageSize = 30;
            Page<Product> productPage;
            if (keyword == null || keyword.trim().isEmpty()) {
                productPage = productService.pageAvailableProducts(pageIndex, pageSize);
            } else {
                productPage = productService.searchAvailableProducts(keyword.trim(), pageIndex, pageSize);
            }

            var list = productPage.getContent().stream().map(p -> {
                Map<String, Object> m = new HashMap<>();
                m.put("orderId", p.getId());
                m.put("title", p.getName());
                m.put("content", p.getDescription());
                m.put("picture", p.getImageUrl());
                m.put("price", p.getPrice());
                m.put("category", p.getCategory());
                m.put("ownName", "平台商家");
                return m;
            }).collect(Collectors.toList());

            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", productPage.getTotalElements());

            resp.put("flag", true);
            resp.put("data", data);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "获取商品失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * GET /order/selectBuys - 获取"我买的"订单（当前用户作为买家的订单）
     */
    @GetMapping("/selectBuys")
    public ResponseEntity<?> selectBuys(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = parseUserId(token);
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }

            // 获取用户作为买家的订单
            List<Order> orders = orderService.getUserOrders(userId);
            
            // 转换为前端需要的格式
            List<Map<String, Object>> orderList = new ArrayList<>();
            for (Order order : orders) {
                Map<String, Object> orderMap = convertOrderToMap(order);
                orderList.add(orderMap);
            }

            response.put("flag", true);
            response.put("data", orderList);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取订单失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /order/selectSells - 获取"我卖的"订单（当前用户作为卖家的订单）
     */
    @GetMapping("/selectSells")
    public ResponseEntity<?> selectSells(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long sellerId = parseUserId(token);
            if (sellerId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }

            // 获取用户作为卖家的订单
            List<Order> orders = orderService.getSellerOrders(sellerId);
            
            // 转换为前端需要的格式
            List<Map<String, Object>> orderList = new ArrayList<>();
            for (Order order : orders) {
                Map<String, Object> orderMap = convertOrderToMap(order);
                orderList.add(orderMap);
            }

            response.put("flag", true);
            response.put("data", orderList);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取订单失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 将Order对象转换为前端需要的Map格式
     */
    private Map<String, Object> convertOrderToMap(Order order) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("id", order.getId());
        orderMap.put("orderId", order.getId());
        orderMap.put("order_id", order.getId());
        orderMap.put("orderNumber", order.getOrderNumber());
        orderMap.put("totalAmount", order.getTotalAmount());
        orderMap.put("status", order.getStatus());
        orderMap.put("createTime", order.getCreateTime());
        orderMap.put("create_time", order.getCreateTime());
        orderMap.put("userId", order.getUserId());
        orderMap.put("sellerId", order.getSellerId());
        orderMap.put("shippingAddress", order.getShippingAddress());
        orderMap.put("receiverName", order.getReceiverName());
        orderMap.put("receiverPhone", order.getReceiverPhone());
        orderMap.put("paymentMethod", order.getPaymentMethod());
        orderMap.put("paymentStatus", order.getPaymentStatus());
        
        // 添加订单项
        List<com.farmporject.backend.trade.model.OrderItem> orderItems = orderService.getOrderItems(order.getId());
        orderMap.put("orderItems", orderItems);
        
        // 为了兼容前端显示，添加一些额外字段
        if (orderItems != null && !orderItems.isEmpty()) {
            com.farmporject.backend.trade.model.OrderItem firstItem = orderItems.get(0);
            orderMap.put("title", firstItem.getProductName());
            orderMap.put("productName", firstItem.getProductName());
            orderMap.put("price", firstItem.getProductPrice());
            orderMap.put("quantity", orderItems.stream().mapToInt(item -> item.getQuantity()).sum());
            orderMap.put("count", orderItems.stream().mapToInt(item -> item.getQuantity()).sum());
        } else {
            orderMap.put("title", "商品名称");
            orderMap.put("productName", "商品名称");
            orderMap.put("price", order.getTotalAmount());
            orderMap.put("quantity", 1);
            orderMap.put("count", 1);
        }
        
        return orderMap;
    }
}



