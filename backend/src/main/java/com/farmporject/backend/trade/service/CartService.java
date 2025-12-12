package com.farmporject.backend.trade.service;

import com.farmporject.backend.trade.model.CartProduct;
import com.farmporject.backend.trade.model.Product;
import com.farmporject.backend.trade.repository.CartProductRepository;
import com.farmporject.backend.trade.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CartService {

    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartProductRepository cartProductRepository, ProductRepository productRepository) {
        this.cartProductRepository = cartProductRepository;
        this.productRepository = productRepository;
    }

    public List<CartProduct> getUserCart(Long userId) {
        return cartProductRepository.findByUserId(userId);
    }

    /**
     * 获取用户购物车，包含商品详情
     */
    public List<Map<String, Object>> getUserCartWithProducts(Long userId) {
        List<CartProduct> cartItems = cartProductRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (CartProduct cartItem : cartItems) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", cartItem.getId());
            item.put("cartId", cartItem.getId());
            item.put("productId", cartItem.getProductId());
            item.put("quantity", cartItem.getQuantity());
            item.put("count", cartItem.getQuantity());
            
            // 获取商品详情
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
            if (product != null) {
                item.put("name", product.getName());
                item.put("title", product.getName());
                item.put("description", product.getDescription());
                item.put("content", product.getDescription());
                item.put("price", product.getPrice());
                item.put("imageUrl", product.getImageUrl());
                item.put("picture", product.getImageUrl());
                item.put("image", product.getImageUrl());
                item.put("category", product.getCategory());
            } else {
                // 商品不存在时的默认值
                item.put("name", "商品已下架");
                item.put("title", "商品已下架");
                item.put("description", "");
                item.put("content", "");
                item.put("price", 0);
                item.put("imageUrl", null);
                item.put("picture", null);
                item.put("image", null);
            }
            
            result.add(item);
        }
        
        return result;
    }

    public CartProduct addToCart(Long userId, Long productId, Integer quantity) {
        CartProduct existingItem = cartProductRepository.findByUserIdAndProductId(userId, productId);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartProductRepository.save(existingItem);
        } else {
            CartProduct cartProduct = new CartProduct();
            cartProduct.setUserId(userId);
            cartProduct.setProductId(productId);
            cartProduct.setQuantity(quantity);
            return cartProductRepository.save(cartProduct);
        }
    }

    public CartProduct updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        CartProduct cartProduct = cartProductRepository.findByUserIdAndProductId(userId, productId);
        if (cartProduct != null) {
            cartProduct.setQuantity(quantity);
            return cartProductRepository.save(cartProduct);
        }
        throw new RuntimeException("Cart item not found");
    }

    public void removeFromCart(Long userId, Long productId) {
        cartProductRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public void clearUserCart(Long userId) {
        cartProductRepository.deleteByUserId(userId);
    }
}