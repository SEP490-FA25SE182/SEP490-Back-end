package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.UserDto;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.model.User;
import com.sep.rookieservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
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
    public List<User> createUsers(List<User> users) {
        return userRepository.saveAll(users);
    }

    public Optional<User> findByEmail(String email) {return  userRepository.findByEmail(email);}

    @Cacheable(value = "User", key = "'id'")
    public Optional<User> findById(String id) {
        System.out.println("⏳ Querying User by id...");
        return  userRepository.findById(id);
    }

    @CacheEvict(value = {"allUsers", "User"}, allEntries = true)
    public User updateUser(String id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // chỉ update những field được phép
        user.setFullName(userDto.getFullName());
        user.setBirthDate(userDto.getBirthDate());
        user.setGender(userDto.getGender());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setAvatarUrl(userDto.getAvatarUrl());
        user.setUpdateAt(Instant.now());
        user.setIsActived(userDto.getIsActived());

        return userRepository.save(user);
    }

    @CacheEvict(value = {"allUsers", "User"}, allEntries = true)
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setIsActived(IsActived.INACTIVE);
        userRepository.save(user);
    }
}

