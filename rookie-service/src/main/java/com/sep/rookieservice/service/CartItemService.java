package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.CartItemDto;
import com.sep.rookieservice.model.Cart;
import com.sep.rookieservice.model.CartItem;
import com.sep.rookieservice.repository.CartItemRepository;
import com.sep.rookieservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Cacheable(value = "allCartItems", key = "'all'")
    public List<CartItem> getAllCartItems() {
        System.out.println("⏳ Querying DB...");
        return cartItemRepository.findAll();
    }

    @Cacheable(value = "CartItem", key = "'id'")
    public Optional<CartItem> findById(String id) {
        return cartItemRepository.findById(id);
    }

    public List<CartItem> findByCartId(String cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    @CacheEvict(value = {"allCartItems", "CartItem", "allCarts", "Cart"}, allEntries = true)
    public CartItem createCartItem(CartItem cartItem) {
        CartItem saved = cartItemRepository.save(cartItem);
        recalcCart(cartItem.getCartId()); // 👉 Cập nhật Cart sau khi thêm item
        return saved;
    }

    @CacheEvict(value = {"allCartItems", "CartItem", "allCarts", "Cart"}, allEntries = true)
    public CartItem updateCartItem(String id, CartItemDto dto) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CartItem not found with id: " + id));

        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());

        CartItem updated = cartItemRepository.save(item);
        recalcCart(item.getCartId());
        return updated;
    }

    @CacheEvict(value = {"allCartItems", "CartItem", "allCarts", "Cart"}, allEntries = true)
    public void deleteCartItem(String id) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CartItem not found with id: " + id));

        String cartId = item.getCartId();
        cartItemRepository.delete(item);

        recalcCart(cartId);
    }

    // Hàm tự động tính lại amount & totalPrice cho Cart
    private void recalcCart(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));

        List<CartItem> items = cartItemRepository.findByCartId(cartId);

        int totalAmount = items.stream().mapToInt(CartItem::getQuantity).sum();
        double totalPrice = items.stream().mapToDouble(i -> i.getQuantity() * i.getPrice()).sum();

        cart.setAmount(totalAmount);
        cart.setTotalPrice(totalPrice);
        cart.setUpdatedAt(Instant.now());

        cartRepository.save(cart);
    }

    // Xoá toàn bộ CartItem trong giỏ hàng
    @CacheEvict(value = {"allCartItems", "CartItem", "allCarts", "Cart"}, allEntries = true)
    public void clearCart(String cartId) {
        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        if (items.isEmpty()) {
            throw new RuntimeException("No items found in cart with id: " + cartId);
        }

        cartItemRepository.deleteAll(items);

        // Sau khi xoá hết item → amount = 0, totalPrice = 0
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));

        cart.setAmount(0);
        cart.setTotalPrice(0.0);
        cart.setUpdatedAt(Instant.now());

        cartRepository.save(cart);
    }

}

