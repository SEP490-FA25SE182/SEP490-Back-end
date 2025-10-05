package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.UserAddressRequest;
import com.sep.rookieservice.dto.UserAddressResponse;
import com.sep.rookieservice.entity.UserAddress;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.UserAddressService;
import com.sep.rookieservice.service.impl.UserAddressServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/addresses")
@RequiredArgsConstructor
@Validated
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping
    public List<UserAddressResponse> getUserAddresses() {
        return userAddressService.getAll();
    }

    @GetMapping("/{id}")
    public UserAddressResponse getUserAddress(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String id) {
        return userAddressService.getById(id);
    }

    // Lấy theo userId
    @GetMapping("/user/{userId}")
    public List<UserAddressResponse> getByUser(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String userId) {
        return userAddressService.getByUserId(userId);
    }

    // CREATE (danh sách)
    @PostMapping
    public List<UserAddressResponse> createUserAddresses(@RequestBody @Valid List<UserAddressRequest> requests) {
        return userAddressService.create(requests);
    }

    // UPDATE
    @PutMapping("/{id}")
    public UserAddressResponse updateUserAddress(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid UserAddressRequest request) {
        return userAddressService.update(id, request);
    }

    // SOFT DELETE
    @DeleteMapping("/{id}")
    public void deleteUserAddress(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        userAddressService.softDelete(id);
    }

    // SEARCH
    @GetMapping("/search")
    public Page<UserAddressResponse> search(
            @RequestParam(required = false) IsActived isActived,
            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return userAddressService.search(isActived, pageable);
    }
}