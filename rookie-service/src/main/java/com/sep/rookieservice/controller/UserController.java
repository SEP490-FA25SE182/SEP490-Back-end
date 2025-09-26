package com.sep.rookieservice.controller;

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
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

}
