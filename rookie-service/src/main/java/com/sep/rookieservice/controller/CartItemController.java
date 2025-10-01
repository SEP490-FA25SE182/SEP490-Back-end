package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.CartItemDto;
import com.sep.rookieservice.model.CartItem;
import com.sep.rookieservice.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/cart/cart-items")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @GetMapping
    public List<CartItem> getCartItems() {
        return cartItemService.getAllCartItems();
    }

    @GetMapping("/{id}")
    public CartItem getCartItem(@PathVariable String id) {
        return cartItemService.findById(id).get();
    }

    @GetMapping("/cart/{cartId}")
    public List<CartItem> getItemsByCart(@PathVariable String cartId) {
        return cartItemService.findByCartId(cartId);
    }

    @PostMapping
    public CartItem createCartItem(@RequestBody CartItem item) {
        return cartItemService.createCartItem(item);
    }

    @PutMapping("/{id}")
    public CartItem updateCartItem(@PathVariable String id, @RequestBody CartItemDto dto) {
        return cartItemService.updateCartItem(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCartItem(@PathVariable String id) {
        cartItemService.deleteCartItem(id);
    }

    // Xoá toàn bộ CartItem trong giỏ hàng
    @DeleteMapping("/cart/{cartId}/clear")
    public void clearCart(@PathVariable String cartId) {
        cartItemService.clearCart(cartId);
    }

}

