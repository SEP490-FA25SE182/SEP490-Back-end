package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.OrderDto;
import com.sep.rookieservice.model.Cart;
import com.sep.rookieservice.model.CartItem;
import com.sep.rookieservice.model.Order;
import com.sep.rookieservice.model.OrderDetail;
import com.sep.rookieservice.enums.OrderEnum;
import com.sep.rookieservice.repository.CartItemRepository;
import com.sep.rookieservice.repository.CartRepository;
import com.sep.rookieservice.repository.OrderDetailRepository;
import com.sep.rookieservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Cacheable(value = "allOrders", key = "'all'")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Cacheable(value = "Order", key = "'id'")
    public Optional<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    @CacheEvict(value = {"allOrders", "Order"}, allEntries = true)
    public List<Order> createOrders(List<Order> orders) {
        return orderRepository.saveAll(orders);
    }

    @CacheEvict(value = {"allOrders", "Order"}, allEntries = true)
    public Order updateOrder(String id, OrderDto dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        order.setAmount(dto.getAmount());
        order.setTotalPrice(dto.getTotalPrice());
        order.setStatus(dto.getStatus());
        order.setUpdatedAt(Instant.now());

        return orderRepository.save(order);
    }

    @CacheEvict(value = {"allOrders", "Order"}, allEntries = true)
    public void deleteOrder(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        orderRepository.delete(order);
    }

    // Move CartItems to Order + OrderDetails
    @Transactional
    @CacheEvict(value = {"allOrders", "Order", "allCartItems", "CartItem", "allCarts", "Cart"}, allEntries = true)
    public Order moveCartToOrder(String cartId, String walletId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));
        List<CartItem> items = cartItemRepository.findByCartId(cartId);

        if (items.isEmpty()) {
            throw new RuntimeException("Cart is empty, cannot create order");
        }

        // Tạo Order
        Order order = Order.builder()
                .amount(cart.getAmount())
                .totalPrice(cart.getTotalPrice())
                .walletId(walletId)
                .cartId(cartId)
                .status(OrderEnum.PENDING.getStatus()) // đặt hàng
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Tạo OrderDetail từ CartItem
        for (CartItem item : items) {
            OrderDetail detail = OrderDetail.builder()
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .orderId(savedOrder.getOrderId())
                    .bookId(item.getBookId())
                    .build();
            orderDetailRepository.save(detail);
        }

        // Clear cart sau khi order
        cartItemRepository.deleteAll(items);
        cart.setAmount(0);
        cart.setTotalPrice(0);
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return savedOrder;
    }
}


