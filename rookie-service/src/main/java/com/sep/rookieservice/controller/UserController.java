package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.UserDto;
import com.sep.rookieservice.model.User;
import com.sep.rookieservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {return userService.findById(id).get();}

    @GetMapping("/{email}")
    public User getUserByEmail(@PathVariable String email) {return userService.findByEmail(email).get();}

    @PostMapping
    public List<User> createUsers(@RequestBody List<User> users) {
        return userService.createUsers(users);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }
}
