package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.CartRequest;
import com.sep.rookieservice.dto.CartResponse;
import com.sep.rookieservice.entity.Cart;
import com.sep.rookieservice.service.CartService;
import com.sep.rookieservice.service.impl.CartServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/carts")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;

    @GetMapping
    public List<CartResponse> getCarts() {
        return cartService.getAll();
    }

    @GetMapping("/{id}")
    public CartResponse getCart(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format") String id) {
        return cartService.getById(id);
    }

    @GetMapping("/user/{userId}")
    public CartResponse getCartByUser(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format") String userId) {
        return cartService.getByUserId(userId);
    }

    @PostMapping
    public List<CartResponse> createCarts(@RequestBody @Valid List<CartRequest> requests) {
        return cartService.create(requests);
    }

    @PutMapping("/{id}")
    public CartResponse updateCart(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid CartRequest request) {
        return cartService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCart(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        cartService.softDelete(id);
    }
}

