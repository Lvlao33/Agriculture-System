package com.farmporject.backend.trade.controller;

import com.farmporject.backend.trade.model.Product;
import com.farmporject.backend.trade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                response.put("flag", false);
                response.put("message", "商品名称不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                response.put("flag", false);
                response.put("message", "商品价格必须大于0");
                return ResponseEntity.badRequest().body(response);
            }

            if (product.getSellerId() == null) {
                response.put("flag", false);
                response.put("message", "卖家ID不能为空");
                return ResponseEntity.badRequest().body(response);
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
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取商品详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}