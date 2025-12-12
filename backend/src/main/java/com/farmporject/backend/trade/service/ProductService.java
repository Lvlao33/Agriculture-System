package com.farmporject.backend.trade.service;

import com.farmporject.backend.trade.model.Product;
import com.farmporject.backend.trade.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAvailableProducts() {
        // 返回所有上架商品（不再强制库存>0，防止库存为空导致前端无数据）
        return productRepository.findByIsAvailableTrue();
    }

    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public Page<Product> pageAvailableProducts(int page, int size) {
        return productRepository.pageAvailableProducts(PageRequest.of(page, size));
    }

    public Page<Product> searchAvailableProducts(String keyword, int page, int size) {
        return productRepository.searchAvailableProducts(keyword, PageRequest.of(page, size));
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByKeyword(keyword);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    if (productDetails.getName() != null) product.setName(productDetails.getName());
                    if (productDetails.getPrice() != null) product.setPrice(productDetails.getPrice());
                    if (productDetails.getStock() != null) product.setStock(productDetails.getStock());
                    if (productDetails.getDescription() != null) product.setDescription(productDetails.getDescription());
                    if (productDetails.getImageUrl() != null) product.setImageUrl(productDetails.getImageUrl());
                    if (productDetails.getCategory() != null) product.setCategory(productDetails.getCategory());
                    if (productDetails.getUnit() != null) product.setUnit(productDetails.getUnit());
                    if (productDetails.getIsAvailable() != null) product.setIsAvailable(productDetails.getIsAvailable());
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public boolean checkStock(Long productId, Integer quantity) {
        Optional<Product> product = productRepository.findById(productId);
        return product.isPresent() && product.get().getStock() >= quantity;
    }
}