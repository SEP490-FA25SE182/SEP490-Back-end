package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.UserRequest;
import com.sep.rookieservice.dto.UserResponse;
import com.sep.rookieservice.entity.User;
import com.sep.rookieservice.service.UserService;
import com.sep.rookieservice.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponse getUser(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String id) {
        return userService.getById(id);
    }

    @GetMapping("/email/{email}")
    public UserResponse getUserByEmail(
            @PathVariable @Email @Size(max = 254) String email) {
        return userService.getByEmail(email);
    }

    // CREATE
    @PostMapping
    public List<UserResponse> createUsers(@RequestBody @Valid List<UserRequest> requests) {
        return userService.create(requests);
    }

    // UPDATE
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid UserRequest request) {
        return userService.update(id, request);
    }

    // SOFT DELETE
    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        userService.softDelete(id);
    }
}