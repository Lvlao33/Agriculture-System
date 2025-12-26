package com.farmporject.backend.trade.controller;

import com.farmporject.backend.trade.model.Product;
import com.farmporject.backend.trade.service.ProductService;
import com.farmporject.backend.security.UserContext;
import com.farmporject.backend.config.JwtConfig;
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
    
    @Autowired
    private JwtConfig jwtConfig;

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

            // 库存解析
            Integer stock = 0;
            Object stockObj = requestBody.get("stock");
            if (stockObj instanceof Number) {
                stock = ((Number) stockObj).intValue();
            } else if (stockObj instanceof String) {
                try {
                    stock = Integer.parseInt((String) stockObj);
                } catch (NumberFormatException e) {
                    // 使用默认值0
                }
            }
            // 库存不能为负数
            if (stock < 0) {
                stock = 0;
            }

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setImageUrl(imageUrl);
            product.setCategory(category);
            product.setSellerId(sellerId);
            product.setStock(stock); // 设置库存

            // 默认值
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

    /**
     * 更新商品
     * 支持字段映射：title -> name, content -> description, picture -> imageUrl
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Map<String, Object> requestBody,
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

            // 检查商品是否存在
            Optional<Product> productOpt = productService.getProductById(id);
            if (!productOpt.isPresent()) {
                response.put("flag", false);
                response.put("message", "商品不存在");
                return ResponseEntity.ok().body(response);
            }

            Product existingProduct = productOpt.get();
            // 检查权限：只有商品所有者可以更新
            if (!existingProduct.getSellerId().equals(userId)) {
                response.put("flag", false);
                response.put("message", "无权修改此商品");
                return ResponseEntity.status(403).body(response);
            }

            // 创建更新对象
            Product productDetails = new Product();
            
            // 字段映射
            String name = (String) requestBody.getOrDefault("title", requestBody.get("name"));
            String description = (String) requestBody.getOrDefault("content", requestBody.get("description"));
            String imageUrl = (String) requestBody.getOrDefault("picture", requestBody.get("imageUrl"));
            String category = (String) requestBody.get("category");

            if (name != null) productDetails.setName(name);
            if (description != null) productDetails.setDescription(description);
            if (imageUrl != null) productDetails.setImageUrl(imageUrl);
            if (category != null) productDetails.setCategory(category);

            // 价格解析
            Object priceObj = requestBody.get("price");
            if (priceObj != null) {
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
                if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                    productDetails.setPrice(price);
                }
            }

            // 库存解析
            Object stockObj = requestBody.get("stock");
            if (stockObj != null) {
                Integer stock = 0;
                if (stockObj instanceof Number) {
                    stock = ((Number) stockObj).intValue();
                } else if (stockObj instanceof String) {
                    try {
                        stock = Integer.parseInt((String) stockObj);
                    } catch (NumberFormatException e) {
                        stock = 0;
                    }
                }
                if (stock >= 0) {
                    productDetails.setStock(stock);
                }
            }

            // 更新商品
            Product updatedProduct = productService.updateProduct(id, productDetails);

            response.put("flag", true);
            response.put("message", "商品更新成功");
            response.put("data", updatedProduct);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "更新商品失败: " + e.getMessage());
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
            // 解析 userId（支持JWT和旧格式token）
            Long userId = parseUserId(token);
            
            if (userId == null) {
                response.put("flag", false);
                response.put("message", "请先登录");
                return ResponseEntity.status(401).body(response);
            }

            // 检查商品是否存在
            Optional<Product> productOpt = productService.getProductById(id);
            if (!productOpt.isPresent()) {
                response.put("flag", false);
                response.put("message", "商品不存在");
                return ResponseEntity.ok().body(response);
            }

            Product product = productOpt.get();
            // 检查权限：只有商品所有者可以下架
            if (!product.getSellerId().equals(userId)) {
                response.put("flag", false);
                response.put("message", "无权下架此商品");
                return ResponseEntity.status(403).body(response);
            }

            // 检查商品是否已经下架
            if (!product.getIsAvailable()) {
                response.put("flag", false);
                response.put("message", "商品已经下架");
                return ResponseEntity.ok().body(response);
            }

            // 执行下架（标记为不可用，而不是物理删除）
            // 这样可以保留历史订单数据，避免外键约束问题
            Product deactivatedProduct = productService.deactivateProduct(id);

            response.put("flag", true);
            response.put("message", "商品下架成功");
            response.put("data", deactivatedProduct);
            return ResponseEntity.ok().body(response);

        } catch (IllegalArgumentException e) {
            response.put("flag", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "下架商品失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}