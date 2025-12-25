package com.farmporject.backend.trade.service;

import com.farmporject.backend.trade.model.Order;
import com.farmporject.backend.trade.model.OrderItem;
import com.farmporject.backend.trade.model.Product;
import com.farmporject.backend.trade.model.CartProduct;
import com.farmporject.backend.trade.repository.OrderRepository;
import com.farmporject.backend.trade.repository.OrderItemRepository;
import com.farmporject.backend.trade.repository.ProductRepository;
import com.farmporject.backend.trade.repository.CartProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ProductRepository productRepository, CartProductRepository cartProductRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartProductRepository = cartProductRepository;
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

    /**
     * 从购物车为每种商品创建独立订单（待付款状态）
     * 每种商品创建一个订单，如果同一商品有多个数量，它们在同一订单中
     * @param userId 用户ID
     * @param productIds 商品ID列表（从购物车中选择的商品）
     * @param shippingAddress 收货地址
     * @param receiverName 收货人姓名
     * @param receiverPhone 收货人电话
     * @return 创建的订单列表
     */
    public List<Order> createOrdersByProductType(Long userId, List<Long> productIds, 
                                                 String shippingAddress, String receiverName, String receiverPhone) {
        if (productIds == null || productIds.isEmpty()) {
            throw new RuntimeException("请选择要购买的商品");
        }

        List<Order> createdOrders = new ArrayList<>();

        // 遍历每个商品ID，为每种商品创建一个订单
        for (Long productId : productIds) {
            // 从购物车获取商品信息
            CartProduct cartItem = cartProductRepository.findByUserIdAndProductId(userId, productId);
            if (cartItem == null) {
                throw new RuntimeException("购物车中不存在商品ID: " + productId);
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + productId));

            if (product.getIsAvailable() == null || !product.getIsAvailable()) {
                throw new RuntimeException("商品已下架: " + product.getName());
            }

            // 检查库存
            Integer quantity = cartItem.getQuantity();
            Integer currentStock = product.getStock() != null ? product.getStock() : 0;
            if (currentStock <= 0) {
                throw new RuntimeException("商品库存为零，无法购买: " + product.getName());
            }
            if (currentStock < quantity) {
                throw new RuntimeException("商品库存不足，当前库存: " + currentStock + "，需要数量: " + quantity + "，商品: " + product.getName());
            }

            // 减少库存
            product.setStock(currentStock - quantity);
            productRepository.save(product);

            // 创建订单项（同一商品的所有数量在一个订单中）
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(productId);
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

            // 创建订单
            Order order = new Order();
            order.setUserId(userId);
            order.setSellerId(product.getSellerId());
            order.setTotalAmount(orderItem.getSubtotal());
            order.setStatus("pending_payment"); // 待付款
            order.setPaymentStatus("PENDING");
            order.setShippingAddress(shippingAddress);
            order.setReceiverName(receiverName);
            order.setReceiverPhone(receiverPhone);
            order.setPaymentMethod("ONLINE"); // 默认在线支付

            // 保存订单和订单项
            Order savedOrder = orderRepository.save(order);
            orderItem.setOrderId(savedOrder.getId());
            orderItemRepository.save(orderItem);

            createdOrders.add(savedOrder);
        }

        return createdOrders;
    }

    /**
     * 从购物车创建订单（待付款状态）
     * @param userId 用户ID
     * @param productIds 商品ID列表（从购物车中选择的商品）
     * @param shippingAddress 收货地址
     * @param receiverName 收货人姓名
     * @param receiverPhone 收货人电话
     * @return 创建的订单
     */
    public Order createOrderFromCart(Long userId, List<Long> productIds, 
                                     String shippingAddress, String receiverName, String receiverPhone) {
        if (productIds == null || productIds.isEmpty()) {
            throw new RuntimeException("请选择要购买的商品");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        Long sellerId = null;

        // 遍历选中的商品，创建订单项
        for (Long productId : productIds) {
            CartProduct cartItem = cartProductRepository.findByUserIdAndProductId(userId, productId);
            if (cartItem == null) {
                throw new RuntimeException("购物车中不存在商品ID: " + productId);
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + productId));

            if (product.getIsAvailable() == null || !product.getIsAvailable()) {
                throw new RuntimeException("商品已下架: " + product.getName());
            }

            // 检查库存
            Integer quantity = cartItem.getQuantity();
            Integer currentStock = product.getStock() != null ? product.getStock() : 0;
            if (currentStock <= 0) {
                throw new RuntimeException("商品库存为零，无法购买: " + product.getName());
            }
            if (currentStock < quantity) {
                throw new RuntimeException("商品库存不足，当前库存: " + currentStock + "，需要数量: " + quantity + "，商品: " + product.getName());
            }

            // 设置卖家ID（取第一个商品的卖家ID）
            if (sellerId == null) {
                sellerId = product.getSellerId();
            }

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(productId);
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        // 减少所有商品的库存（在创建订单之前）
        for (Long productId : productIds) {
            CartProduct cartItem = cartProductRepository.findByUserIdAndProductId(userId, productId);
            if (cartItem != null) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + productId));
                Integer quantity = cartItem.getQuantity();
                Integer currentStock = product.getStock() != null ? product.getStock() : 0;
                product.setStock(currentStock - quantity);
                productRepository.save(product);
            }
        }

        if (sellerId == null) {
            throw new RuntimeException("无法确定卖家信息");
        }

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setSellerId(sellerId);
        order.setTotalAmount(totalAmount);
        order.setStatus("pending_payment"); // 待付款
        order.setPaymentStatus("PENDING");
        order.setShippingAddress(shippingAddress);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setPaymentMethod("ONLINE"); // 默认在线支付

        // 保存订单和订单项
        Order savedOrder = orderRepository.save(order);
        for (OrderItem item : orderItems) {
            item.setOrderId(savedOrder.getId());
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    /**
     * 订单付款：将待付款订单更新为待发货状态
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    public Order payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在，ID: " + orderId));

        if (!"pending_payment".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法付款");
        }

        order.setStatus("pending_shipment"); // 待发货
        order.setPaymentStatus("PAID"); // 已付款

        return orderRepository.save(order);
    }

    /**
     * 确认发货：将待发货订单更新为待收货状态
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    public Order shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在，ID: " + orderId));

        if (!"pending_shipment".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法发货");
        }

        order.setStatus("pending_receipt"); // 待收货

        return orderRepository.save(order);
    }

    /**
     * 确认收货：将待收货订单更新为待评价状态
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    public Order confirmReceipt(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在，ID: " + orderId));

        if (!"pending_receipt".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法确认收货");
        }

        order.setStatus("pending_review"); // 待评价

        return orderRepository.save(order);
    }

    /**
     * 完成评价：将待评价订单更新为退款/售后状态
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    public Order completeReview(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在，ID: " + orderId));

        if (!"pending_review".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法完成评价");
        }

        order.setStatus("refund_after_sale"); // 退款/售后

        return orderRepository.save(order);
    }

    /**
     * 直接从商品创建订单（立即购买，不经过购物车）
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 购买数量
     * @param shippingAddress 收货地址
     * @param receiverName 收货人姓名
     * @param receiverPhone 收货人电话
     * @return 创建的订单
     */
    public Order createOrderDirectly(Long userId, Long productId, Integer quantity,
                                     String shippingAddress, String receiverName, String receiverPhone) {
        if (productId == null) {
            throw new RuntimeException("商品ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            quantity = 1; // 默认数量为1
        }

        // 获取商品信息
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + productId));

        if (product.getIsAvailable() == null || !product.getIsAvailable()) {
            throw new RuntimeException("商品已下架: " + product.getName());
        }

        // 检查库存
        Integer currentStock = product.getStock() != null ? product.getStock() : 0;
        if (currentStock <= 0) {
            throw new RuntimeException("商品库存为零，无法购买");
        }
        if (currentStock < quantity) {
            throw new RuntimeException("商品库存不足，当前库存: " + currentStock + "，需要数量: " + quantity);
        }

        // 减少库存
        product.setStock(currentStock - quantity);
        productRepository.save(product);

        // 创建订单项
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(productId);
        orderItem.setProductName(product.getName());
        orderItem.setProductPrice(product.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setSellerId(product.getSellerId());
        order.setTotalAmount(orderItem.getSubtotal());
        order.setStatus("pending_payment"); // 待付款
        order.setPaymentStatus("PENDING");
        order.setShippingAddress(shippingAddress);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setPaymentMethod("ONLINE"); // 默认在线支付

        // 保存订单和订单项
        Order savedOrder = orderRepository.save(order);
        orderItem.setOrderId(savedOrder.getId());
        orderItemRepository.save(orderItem);

        return savedOrder;
    }

}