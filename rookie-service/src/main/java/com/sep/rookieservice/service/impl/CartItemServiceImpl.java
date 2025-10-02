package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.CartItemRequest;
import com.sep.rookieservice.dto.CartItemResponse;
import com.sep.rookieservice.entity.Cart;
import com.sep.rookieservice.entity.CartItem;
import com.sep.rookieservice.mapper.CartItemMapper;
import com.sep.rookieservice.repository.CartItemRepository;
import com.sep.rookieservice.repository.CartRepository;
import com.sep.rookieservice.service.CartItemService;
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
@Transactional
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CartItemMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allCartItems", key = "'all'")
    public List<CartItemResponse> getAll() {
        return cartItemRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "CartItem", key = "#id")
    public CartItemResponse getById(String id) {
        var item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CartItem not found: " + id));
        return mapper.toResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponse> getByCartId(String cartId) {
        return cartItemRepository.findByCartId(cartId).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allCartItems", "CartItem", "allCarts", "Cart"}, allEntries = true)
    public CartItemResponse create(String cartId, CartItemRequest request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found: " + cartId));

        var item = new CartItem();
        mapper.copyForCreate(request, item);
        item.setCartId(cart.getCartId());

        var saved = cartItemRepository.save(item);
        recalcCart(cart.getCartId());
        return mapper.toResponse(saved);
    }

    @Override
    @CacheEvict(value = {"allCartItems", "CartItem", "allCarts", "Cart"}, allEntries = true)
    public CartItemResponse update(String id, CartItemRequest request) {
        var item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CartItem not found: " + id));

        mapper.copyForUpdate(request, item);
        var updated = cartItemRepository.save(item);
        recalcCart(item.getCartId());
        return mapper.toResponse(updated);
    }

    @Override
    @CacheEvict(value = {"allCartItems", "CartItem", "allCarts", "Cart"}, allEntries = true)
    public void delete(String id) {
        var item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CartItem not found: " + id));

        String cartId = item.getCartId();
        cartItemRepository.delete(item);
        recalcCart(cartId);
    }

    @Override
    @CacheEvict(value = {"allCartItems", "CartItem", "allCarts", "Cart"}, allEntries = true)
    public void clearCart(String cartId) {
        var items = cartItemRepository.findByCartId(cartId);
        if (items.isEmpty()) {
             throw new RuntimeException("No items found in cart: " + cartId);
        } else {
            cartItemRepository.deleteAll(items);
        }
        // Reset cart
        var cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found: " + cartId));
        cart.setAmount(0);
        cart.setTotalPrice(0.0);
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
    }

    // Tính lại amount & totalPrice cho Cart
    private void recalcCart(String cartId) {
        var cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found: " + cartId));
        var items = cartItemRepository.findByCartId(cartId);
        int totalAmount = items.stream().mapToInt(CartItem::getQuantity).sum();
        double totalPrice = items.stream().mapToDouble(i -> i.getQuantity() * i.getPrice()).sum();
        cart.setAmount(totalAmount);
        cart.setTotalPrice(totalPrice);
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
    }
}