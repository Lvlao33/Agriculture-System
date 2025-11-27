package com.farmporject.backend.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.farmporject.backend.trade.model.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 预留：接入 JPA/MyBatis 后操作数据库
}
