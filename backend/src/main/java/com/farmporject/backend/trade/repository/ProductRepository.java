package com.farmporject.backend.trade.repository;

import com.farmporject.backend.trade.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySellerId(Long sellerId);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategory(String category);
    List<Product> findByIsAvailableTrue();
    List<Product> findBySellerIdAndIsAvailableTrue(Long sellerId);

    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.isAvailable = true")
    List<Product> findAvailableProducts();

    @Query("SELECT p FROM Product p WHERE p.isAvailable = true")
    Page<Product> pageAvailableProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isAvailable = true AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchAvailableProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> findByKeyword(@Param("keyword") String keyword);
}