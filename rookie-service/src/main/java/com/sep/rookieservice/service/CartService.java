package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.CartDto;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.model.Cart;
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
public class CartService {
    private final CartRepository cartRepository;

    @Cacheable(value = "allCarts", key = "'all'")
    public List<Cart> getAllCarts() {
        System.out.println("⏳ Querying DB...");
        return cartRepository.findAll();
    }

    @CacheEvict(value = "allCarts", allEntries = true)
    public List<Cart> createCarts(List<Cart> carts) {
        return cartRepository.saveAll(carts);
    }

    @Cacheable(value = "Cart", key = "'id'")
    public Optional<Cart> findById(String id) {
        System.out.println("⏳ Querying Cart by id...");
        return cartRepository.findById(id);
    }

    @CacheEvict(value = {"allCarts", "Cart"}, allEntries = true)
    public Cart updateCart(String id, CartDto dto) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + id));

        cart.setAmount(dto.getAmount());
        cart.setTotalPrice(dto.getTotalPrice());
        cart.setIsActived(dto.getIsActived());
        cart.setUpdatedAt(Instant.now());

        return cartRepository.save(cart);
    }

    @CacheEvict(value = {"allCarts", "Cart"}, allEntries = true)
    public void deleteCart(String id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + id));

        cart.setIsActived(IsActived.INACTIVE);
        cart.setUpdatedAt(Instant.now());

        cartRepository.save(cart);
    }
}

