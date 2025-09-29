package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.UserAddressDto;
import com.sep.rookieservice.model.UserAddress;
import com.sep.rookieservice.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/addresses")
@RequiredArgsConstructor
public class UserAddressController {
    private final UserAddressService userAddressService;

    @GetMapping
    public List<UserAddress> getUserAddresses() {
        return userAddressService.getAllUserAddresses();
    }

    @GetMapping("/{id}")
    public UserAddress getUserAddress(@PathVariable String id) {
        return userAddressService.findById(id).get();
    }

    @PostMapping
    public List<UserAddress> createUserAddresses(@RequestBody List<UserAddress> addresses) {
        return userAddressService.createUserAddresses(addresses);
    }

    @PutMapping("/{id}")
    public UserAddress updateUserAddress(@PathVariable String id, @RequestBody UserAddressDto dto) {
        return userAddressService.updateUserAddress(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteUserAddress(@PathVariable String id) {
        userAddressService.deleteUserAddress(id);
    }
}

