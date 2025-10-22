package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.OrderRequest;
import com.sep.rookieservice.dto.OrderResponse;
import com.sep.rookieservice.entity.*;
import com.sep.rookieservice.enums.OrderEnum;
import com.sep.rookieservice.mapper.OrderMapper;
import com.sep.rookieservice.repository.*;
import com.sep.rookieservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final double MAX_DISCOUNT_RATE = 0.10; // tối đa 10%
    private static final double COIN_VALUE = 1.0;

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final WalletRepository walletRepository;
    private final OrderMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allOrders", key = "'all'")
    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Order", key = "#id")
    public OrderResponse getById(String id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return mapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getByCartId(String cartId) {
        return orderRepository.findByCartId(cartId).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getByWalletId(String walletId) {
        return orderRepository.findByWalletId(walletId).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allOrders", "Order"}, allEntries = true)
    public List<OrderResponse> create(List<OrderRequest> requests) {
        var entities = requests.stream().map(req -> {
            validateStatus(req.getStatus());
            var o = new Order();
            mapper.copyForCreate(req, o);
            if (o.getCreatedAt() == null) o.setCreatedAt(Instant.now());
            o.setUpdatedAt(Instant.now());
            return o;
        }).toList();
        return orderRepository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allOrders", "Order"}, allEntries = true)
    public OrderResponse update(String id, OrderRequest request) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));

        validateStatus(request.getStatus());

        mapper.copyForUpdate(request, order);
        order.setUpdatedAt(Instant.now());

        return mapper.toResponse(orderRepository.save(order));
    }

    @Override
    @CacheEvict(value = {"allOrders", "Order"}, allEntries = true)
    public void delete(String id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));

        var current = OrderEnum.getByStatus(order.getStatus());
        if (current == OrderEnum.DELIVERED || current == OrderEnum.RETURNED) {
            throw new IllegalStateException("Cannot cancel an order that is delivered/returned");
        }
        // Soft delete: chuyển trạng thái sang CANCELLED
        order.setStatus(OrderEnum.CANCELLED.getStatus());
        order.setUpdatedAt(Instant.now());

        orderRepository.save(order);
    }

    // Move CartItems -> Order + OrderDetails
    @Override
    @Transactional
    @CacheEvict(value = {"allOrders", "Order", "allCartItems", "CartItem", "allCarts", "Cart"}, allEntries = true)
    public OrderResponse moveCartToOrder(String cartId, String walletId, boolean usePoints) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));
        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        if (items.isEmpty()) throw new RuntimeException("Cart is empty, cannot create order");

        // Lấy ví để trừ coin (nếu có dùng điểm)
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        double cartTotal = cart.getTotalPrice();

        // Tính giảm giá từ coin
        int coinsToUse = 0;
        double discount = 0.0;

        if (usePoints) {
            double maxDiscountByRate = cartTotal * MAX_DISCOUNT_RATE;
            double maxDiscountByCoins = wallet.getCoin() * COIN_VALUE;

            // số tiền có thể giảm là min
            double allowedDiscount = Math.min(maxDiscountByRate, maxDiscountByCoins);

            // quy ra số coin nguyên sẽ dùng
            coinsToUse = (int) Math.floor(allowedDiscount / COIN_VALUE);
            if (coinsToUse > 0) {
                discount = coinsToUse * COIN_VALUE;
            }
        }

        double finalTotal = Math.max(0.0, cartTotal - discount);

        Order order = Order.builder()
                .amount(cart.getAmount())
                .totalPrice(finalTotal)
                .walletId(walletId)
                .cartId(cartId)
                .status(OrderEnum.PENDING.getStatus())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        for (CartItem item : items) {
            OrderDetail detail = OrderDetail.builder()
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .orderId(savedOrder.getOrderId())
                    .bookId(item.getBookId())
                    .build();
            orderDetailRepository.save(detail);
        }

        // Trừ coin nếu có dùng
        if (coinsToUse > 0) {
            wallet.setCoin(wallet.getCoin() - coinsToUse);
            wallet.setUpdatedAt(Instant.now());
            walletRepository.save(wallet);
        }

        // clear cart
         cartItemRepository.deleteAll(items);
         cart.setAmount(0);
         cart.setTotalPrice(0);
         cart.setUpdatedAt(Instant.now());
         cartRepository.save(cart);

        return mapper.toResponse(savedOrder);
    }

    private void validateStatus(Byte status) {
        // ném lỗi nếu status không hợp lệ
        OrderEnum.getByStatus(status);
    }

    @Override
    public Page<OrderResponse> search(OrderEnum status, Pageable pageable) {
        Order probe = new Order();
        if (status != null) {
            probe.setStatus(status.getStatus()); // convert enum -> byte
        }

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnorePaths("orderId", "amount", "totalPrice", "updatedAt", "createdAt",
                        "walletId", "cartId", "wallet", "cart", "orderDetails", "transaction")
                .withIgnoreNullValues();

        Example<Order> example = Example.of(probe, matcher);

        return orderRepository.findAll(example, pageable)
                .map(mapper::toResponse);
    }
}