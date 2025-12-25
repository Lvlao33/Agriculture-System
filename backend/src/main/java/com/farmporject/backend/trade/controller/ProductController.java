package com.farmporject.backend.trade.controller;

import com.farmporject.backend.trade.model.Product;
import com.farmporject.backend.trade.service.ProductService;
import com.farmporject.backend.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 交易模块-商品管理
 * 路由前缀：/api/trade/products
 * - GET /api/trade/products 商品列表
 * - POST /api/trade/products 新建商品
 * - GET /api/trade/products/{id} 商品详情
 * - DELETE /api/trade/products/{id} 删除商品
 */
@RestController
@RequestMapping("/api/trade/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) String keyword) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Product> products;

            if (sellerId != null) {
                products = productService.getProductsBySeller(sellerId);
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                products = productService.searchProducts(keyword.trim());
            } else {
                products = productService.getAvailableProducts();
            }

            response.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", products);
            data.put("total", products.size());

            response.put("data", data);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取商品列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 兼容前端字段与 token 解析的商品创建
     * 支持字段映射：
     * - title -> name
     * - content -> description
     * - picture -> imageUrl
     */
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 解析 sellerId（token 形如 tk_{userId}_{username}）
            // 优先使用UserContext（如果JWT过滤器已填充）
            Long sellerId = null;
            if (UserContext.isAuthenticated()) {
                sellerId = UserContext.getCurrentUserId();
            }
            
            // 如果UserContext中没有userId，则从token中解析
            if (sellerId == null && token != null) {
                // 移除Bearer前缀（如果存在）
                String cleanToken = token;
                if (token.startsWith("Bearer ") || token.startsWith("bearer ")) {
                    cleanToken = token.substring(7).trim();
                }
                // 检查token格式：tk_{userId}_{username}
                if (cleanToken.startsWith("tk_")) {
                    String[] parts = cleanToken.split("_");
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

            // 价格解析
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

            // 默认值
            if (product.getStock() == null) {
                product.setStock(0);
            }
            if (product.getIsAvailable() == null) {
                product.setIsAvailable(true);
            }

            Product createdProduct = productService.createProduct(product);

            response.put("flag", true);
            response.put("message", "商品创建成功");
            response.put("data", createdProduct);
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "创建商品失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Product> product = productService.getProductById(id);
            if (product.isPresent()) {
                response.put("flag", true);
                response.put("data", product.get());
                return ResponseEntity.ok().body(response);
            } else {
                response.put("flag", false);
                response.put("message", "商品不存在");
                return ResponseEntity.ok().body(response); // 改为返回200，但flag为false
            }
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取商品详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除商品（下架）
     * 从Authorization header中解析userId，只有商品所有者可以删除
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 解析userId
            Long userId = null;
            if (token != null) {
                // 移除Bearer前缀（如果存在）
                String cleanToken = token;
                if (token.startsWith("Bearer ") || token.startsWith("bearer ")) {
                    cleanToken = token.substring(7).trim();
                }
                // 检查token格式：tk_{userId}_{username}
                if (cleanToken.startsWith("tk_")) {
                    String[] parts = cleanToken.split("_");
                    if (parts.length >= 2) {
                        try {
                            userId = Long.parseLong(parts[1]);
                        } catch (NumberFormatException e) {
                            response.put("flag", false);
                            response.put("message", "无效的 token 格式");
                            return ResponseEntity.badRequest().body(response);
                        }
                    }
                }
            }
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查商品是否存在
            Optional<Product> productOpt = productService.getProductById(id);
            if (!productOpt.isPresent()) {
                response.put("flag", false);
                response.put("message", "商品不存在");
                return ResponseEntity.ok().body(response);
            }

            Product product = productOpt.get();
            // 检查权限：只有商品所有者可以删除
            if (!product.getSellerId().equals(userId)) {
                response.put("flag", false);
                response.put("message", "无权删除此商品");
                return ResponseEntity.badRequest().body(response);
            }

            // 执行删除
            productService.deleteProduct(id);

            response.put("flag", true);
            response.put("message", "商品删除成功");
            return ResponseEntity.ok().body(response);

        } catch (IllegalArgumentException e) {
            response.put("flag", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "删除商品失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}