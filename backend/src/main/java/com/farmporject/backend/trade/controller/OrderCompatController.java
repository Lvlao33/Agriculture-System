package com.farmporject.backend.trade.controller;

import com.farmporject.backend.trade.model.Product;
import com.farmporject.backend.trade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
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

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Map<String, Object> response = new HashMap<>();
        try {
            String type = (String) requestBody.get("type");

            // 仅处理 goods 类型，其余可按需扩展
            if (!"goods".equals(type)) {
                response.put("flag", false);
                response.put("message", "暂不支持的类型: " + type);
                return ResponseEntity.badRequest().body(response);
            }

            // 解析 sellerId
            Long sellerId = null;
            if (token != null && token.startsWith("tk_")) {
                String[] parts = token.split("_");
                if (parts.length >= 2) {
                    try {
                        sellerId = Long.parseLong(parts[1]);
                    } catch (NumberFormatException e) {
                        response.put("flag", false);
                        response.put("message", "无效的 token 格式");
                        return ResponseEntity.badRequest().body(response);
                    }
                }
            }
            if (sellerId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.badRequest().body(response);
            }

            // 字段映射
            String name = (String) requestBody.getOrDefault("title", requestBody.get("name"));
            String description = (String) requestBody.getOrDefault("content", requestBody.get("description"));
            String imageUrl = (String) requestBody.getOrDefault("picture", requestBody.get("imageUrl"));
            String category = (String) requestBody.get("category");

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
                    response.put("flag", false);
                    response.put("message", "价格格式错误");
                    return ResponseEntity.badRequest().body(response);
                }
            }
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

            Product createdProduct = productService.createProduct(product);

            response.put("flag", true);
            response.put("message", "发布成功");
            response.put("data", createdProduct);
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "发布失败: " + e.getMessage());
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
}



