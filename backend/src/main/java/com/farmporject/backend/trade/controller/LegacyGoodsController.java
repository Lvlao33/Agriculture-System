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
 * ����ǰ�˾ɰ桰����/��Դ���ӿڣ�
 * - POST /order                ������Ʒ��type=goods��
 * - GET  /order/goods/{page}   ��ҳ��ȡ��Ʒ�б�
 * - GET  /order/searchGoodsByKeys/{keys}/{page}  �ؼ�������
 *
 * ������ص��µ� products �����ֶ�����С����ӳ�䡣
 */
@RestController
// Legacy endpoints for old frontend; mounted under /legacy to avoid route clash
@RequestMapping("/legacy")
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
                resp.put("message", "Only goods type is supported");
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
                resp.put("message", "Title cannot be empty");
                return ResponseEntity.badRequest().body(resp);
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                resp.put("flag", false);
                resp.put("message", "Price must be greater than 0");
                return ResponseEntity.badRequest().body(resp);
            }

            Product product = new Product();
            product.setName(title);
            product.setDescription(content);
            product.setImageUrl(picture);
            product.setCategory(category);
            product.setPrice(price);
            // legacy: frontend did not send sellerId; use 0 as placeholder
            product.setSellerId(0L);
            product.setUnit("unit");
            product.setStock(9999);
            product.setIsAvailable(true);

            Product created = productService.createProduct(product);
            resp.put("flag", true);
            resp.put("message", "Created successfully");
            resp.put("data", created);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "Create failed: " + e.getMessage());
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
                m.put("ownName", "Platform");
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
            resp.put("message", "Query failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }
}













