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
 * 兼容前端旧版“订单/货源”接口：
 * - POST /order                发布商品（type=goods）
 * - GET  /order/goods/{page}   分页获取商品列表
 * - GET  /order/searchGoodsByKeys/{keys}/{page}  关键词搜索
 *
 * 数据落地到新的 products 表，字段做最小兼容映射。
 */
@RestController
@RequestMapping
public class LegacyGoodsController {

    private final ProductService productService;

    @Autowired
    public LegacyGoodsController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/order")
    public ResponseEntity<?> createLegacyGoods(@RequestBody Map<String, Object> body) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String type = body.getOrDefault("type", "goods").toString();
            if (!"goods".equalsIgnoreCase(type)) {
                resp.put("flag", false);
                resp.put("message", "当前仅支持发布货源商品");
                return ResponseEntity.badRequest().body(resp);
            }

            String title = (String) body.get("title");
            String content = (String) body.get("content");
            String picture = (String) body.get("picture");
            String category = (String) body.get("category");
            BigDecimal price = null;
            try {
                Object p = body.get("price");
                if (p != null) {
                    price = new BigDecimal(p.toString());
                }
            } catch (Exception ignore) {}

            if (title == null || title.trim().isEmpty()) {
                resp.put("flag", false);
                resp.put("message", "商品标题不能为空");
                return ResponseEntity.badRequest().body(resp);
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                resp.put("flag", false);
                resp.put("message", "商品价格必须大于0");
                return ResponseEntity.badRequest().body(resp);
            }

            Product product = new Product();
            product.setName(title);
            product.setDescription(content);
            product.setImageUrl(picture);
            product.setCategory(category);
            product.setPrice(price);
            // 兼容：前端未传 sellerId，这里用 0 占位
            product.setSellerId(0L);
            product.setUnit("件");
            product.setStock(9999);
            product.setIsAvailable(true);

            Product created = productService.createProduct(product);
            resp.put("flag", true);
            resp.put("message", "发布成功");
            resp.put("data", created);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "发布失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @GetMapping("/order/goods/{page}")
    public ResponseEntity<?> listLegacyGoods(@PathVariable("page") int page) {
        return listWithKeyword(null, page);
    }

    @GetMapping("/order/searchGoodsByKeys/{keys}/{page}")
    public ResponseEntity<?> searchLegacyGoods(@PathVariable("keys") String keys,
                                               @PathVariable("page") int page) {
        return listWithKeyword(keys, page);
    }

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
                m.put("ownName", "平台卖家");
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













