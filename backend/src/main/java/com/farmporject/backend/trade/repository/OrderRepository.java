package com.farmporject.backend.trade.repository;

import com.farmporject.backend.trade.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreateTimeDesc(Long userId);
    List<Order> findBySellerIdOrderByCreateTimeDesc(Long sellerId);
    List<Order> findByStatusOrderByCreateTimeDesc(String status);
    List<Order> findByUserIdAndStatusOrderByCreateTimeDesc(Long userId, String status);
    List<Order> findBySellerIdAndStatusOrderByCreateTimeDesc(Long sellerId, String status);
    List<Order> findByPaymentStatusOrderByCreateTimeDesc(String paymentStatus);
}