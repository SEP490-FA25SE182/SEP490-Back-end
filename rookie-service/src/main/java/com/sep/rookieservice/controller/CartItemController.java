package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.CartItemRequest;
import com.sep.rookieservice.dto.CartItemResponse;
import com.sep.rookieservice.entity.CartItem;
import com.sep.rookieservice.service.CartItemService;
import com.sep.rookieservice.service.impl.CartItemServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/carts/cart-items")
@RequiredArgsConstructor
@Validated
public class CartItemController {

    private final CartItemService cartItemService;

    @GetMapping
    public List<CartItemResponse> getCartItems() {
        return cartItemService.getAll();
    }

    @GetMapping("/{id}")
    public CartItemResponse getCartItem(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format") String id) {
        return cartItemService.getById(id);
    }

    @GetMapping("/cart/{cartId}")
    public List<CartItemResponse> getByCart(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format") String cartId) {
        return cartItemService.getByCartId(cartId);
    }

    // CREATE 1 item cho cart
    @PostMapping("/cart/{cartId}")
    public CartItemResponse createCartItem(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String cartId,
            @RequestBody @Valid CartItemRequest request) {
        return cartItemService.create(cartId, request);
    }

    // UPDATE item
    @PutMapping("/{id}")
    public CartItemResponse updateCartItem(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid CartItemRequest request) {
        return cartItemService.update(id, request);
    }

    // DELETE 1 item
    @DeleteMapping("/{id}")
    public void deleteCartItem(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        cartItemService.delete(id);
    }

    // CLEAR cả cart
    @DeleteMapping("/cart/{cartId}/clear")
    public void clearCart(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String cartId) {
        cartItemService.clearCart(cartId);
    }
}