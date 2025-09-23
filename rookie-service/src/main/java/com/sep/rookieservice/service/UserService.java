package com.sep.rookieservice.service;

import com.sep.rookieservice.model.User;
import com.sep.rookieservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Cacheable(value = "allUsers", key = "'all'")
    public List<User> getAllUsers() {
        System.out.println("⏳ Querying DB...");
        return userRepository.findAll();
    }

    @CacheEvict(value = "allUsers", allEntries = true)
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {return  userRepository.findByEmail(email);}

    public Optional<User> findById(String id) {return  userRepository.findById(id);}
}

