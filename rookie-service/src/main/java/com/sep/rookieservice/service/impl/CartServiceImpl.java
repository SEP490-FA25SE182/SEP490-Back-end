package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.CartRequest;
import com.sep.rookieservice.dto.CartResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.entity.Cart;
import com.sep.rookieservice.mapper.CartMapper;
import com.sep.rookieservice.repository.CartRepository;
import com.sep.rookieservice.service.CartService;
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
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allCarts", key = "'all'")
    public List<CartResponse> getAll() {
        return cartRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cart", key = "#id")
    public CartResponse getById(String id) {
        var c = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found: " + id));
        return mapper.toResponse(c);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getByUserId(String userId) {
        var c = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for userId: " + userId));
        return mapper.toResponse(c);
    }

    @Override
    @CacheEvict(value = {"allCarts", "Cart"}, allEntries = true)
    public List<CartResponse> create(List<CartRequest> requests) {
        var entities = requests.stream().map(req -> {
            var c = new Cart();
            mapper.copyForCreate(req, c);
            if (c.getIsActived() == null) c.setIsActived(IsActived.ACTIVE);
            if (c.getCreatedAt() == null) c.setCreatedAt(Instant.now());
            c.setUpdatedAt(Instant.now());
            return c;
        }).toList();
        return cartRepository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allCarts", "Cart"}, allEntries = true)
    public CartResponse update(String id, CartRequest request) {
        var cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found: " + id));

        mapper.copyForUpdate(request, cart);
        cart.setUpdatedAt(Instant.now());

        return mapper.toResponse(cartRepository.save(cart));
    }

    @Override
    @CacheEvict(value = {"allCarts", "Cart"}, allEntries = true)
    public void softDelete(String id) {
        var cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found: " + id));
        cart.setIsActived(IsActived.INACTIVE);
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
    }

    @Override
    public Page<CartResponse> search(IsActived isActived, Pageable pageable) {
        Cart probe = new Cart();
        if (isActived != null) probe.setIsActived(isActived);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnorePaths("cartId", "amount", "totalPrice", "updatedAt", "createdAt",
                        "userId", "user", "cartItems", "orders")
                .withIgnoreNullValues();

        Example<Cart> example = Example.of(probe, matcher);

        return cartRepository.findAll(example, pageable)
                .map(mapper::toResponse);
    }
}