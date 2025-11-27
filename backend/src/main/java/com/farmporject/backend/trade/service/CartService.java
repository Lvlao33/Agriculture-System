package com.farmporject.backend.trade.service;

import com.farmporject.backend.trade.model.CartProduct;
import com.farmporject.backend.trade.repository.CartProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartProductRepository cartProductRepository;

    @Autowired
    public CartService(CartProductRepository cartProductRepository) {
        this.cartProductRepository = cartProductRepository;
    }

    public List<CartProduct> getUserCart(Long userId) {
        return cartProductRepository.findByUserId(userId);
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