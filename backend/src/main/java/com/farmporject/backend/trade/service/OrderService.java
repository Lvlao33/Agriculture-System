package com.farmporject.backend.trade.service;

import com.farmporject.backend.trade.model.Order;
import com.farmporject.backend.trade.model.OrderItem;
import com.farmporject.backend.trade.repository.OrderRepository;
import com.farmporject.backend.trade.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public List<Order> getSellerOrders(Long sellerId) {
        return orderRepository.findBySellerIdOrderByCreateTimeDesc(sellerId);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatusOrderByCreateTimeDesc(status);
    }

    public List<Order> getUserOrdersByStatus(Long userId, String status) {
        return orderRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId, status);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order createOrderWithItems(Order order, List<OrderItem> orderItems) {
        Order savedOrder = orderRepository.save(order);
        for (OrderItem item : orderItems) {
            item.setOrderId(savedOrder.getId());
            orderItemRepository.save(item);
        }
        return savedOrder;
    }

    public Order updateOrderStatus(Long id, String status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public void deleteOrder(Long id) {
        orderItemRepository.deleteByOrderId(id);
        orderRepository.deleteById(id);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}