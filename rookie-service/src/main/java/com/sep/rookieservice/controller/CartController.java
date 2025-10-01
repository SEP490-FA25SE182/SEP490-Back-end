package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.CartDto;
import com.sep.rookieservice.model.Cart;
import com.sep.rookieservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public List<Cart> getCarts() {
        return cartService.getAllCarts();
    }

    @GetMapping("/{id}")
    public Cart getCart(@PathVariable String id) {
        return cartService.findById(id).get();
    }

    @PostMapping
    public List<Cart> createCarts(@RequestBody List<Cart> carts) {
        return cartService.createCarts(carts);
    }

    @PutMapping("/{id}")
    public Cart updateCart(@PathVariable String id, @RequestBody CartDto dto) {
        return cartService.updateCart(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCart(@PathVariable String id) {
        cartService.deleteCart(id);
    }
}

