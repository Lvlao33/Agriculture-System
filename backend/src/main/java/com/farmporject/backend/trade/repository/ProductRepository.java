package com.farmporject.backend.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.farmporject.backend.trade.model.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
