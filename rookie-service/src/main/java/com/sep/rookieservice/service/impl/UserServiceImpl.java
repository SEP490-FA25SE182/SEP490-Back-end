package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.UserRequest;
import com.sep.rookieservice.dto.UserResponse;
import com.sep.rookieservice.entity.Role;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.entity.User;
import com.sep.rookieservice.mapper.UserMapper;
import com.sep.rookieservice.repository.UserRepository;
import com.sep.rookieservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Qualifier("userMapper")
    private final UserMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allUsers", key = "'all'")
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "User", key = "#id")
    public UserResponse getById(String id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return mapper.toResponse(u);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return mapper.toResponse(u);
    }

    @Override
    @CacheEvict(value = {"allUsers", "User"}, allEntries = true)
    public List<UserResponse> create(List<UserRequest> requests) {
        List<User> entities = requests.stream().map(req -> {
            if (req.getEmail() == null || req.getEmail().isBlank())
                throw new IllegalArgumentException("email is required");
            if (req.getPassword() == null || req.getPassword().length() < 8)
                throw new IllegalArgumentException("password (min 8) is required");
            if (req.getRoleId() == null || req.getRoleId().isBlank())
                throw new IllegalArgumentException("roleId is required");

            User u = new User();
            mapper.copyForCreate(req, u);
            u.setPassword("{noop}" + u.getPassword());
            if (u.getIsActived() == null) u.setIsActived(IsActived.ACTIVE);
            if (u.getCreatedAt() == null) u.setCreatedAt(Instant.now());
            u.setUpdateAt(Instant.now());
            return u;
        }).toList();

        return userRepository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allUsers", "User"}, allEntries = true)
    public UserResponse update(String id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        mapper.copyForUpdate(request, user);
        user.setUpdateAt(Instant.now());

        return mapper.toResponse(userRepository.save(user));
    }

    @Override
    @CacheEvict(value = {"allUsers", "User"}, allEntries = true)
    public void softDelete(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setIsActived(IsActived.INACTIVE);
        user.setUpdateAt(Instant.now());
        userRepository.save(user);
    }

    @Override
    public Page<UserResponse> search(String gender, String roleName, IsActived isActived, Pageable pageable) {
        // Chuẩn hoá input: trim → null nếu rỗng
        String g = normalize(gender);
        String rn = normalize(roleName);

        // ---- Probe ----
        User probe = new User();
        if (g != null) probe.setGender(g);
        if (isActived != null) probe.setIsActived(isActived);
        if (rn != null) {
            Role r = new Role();
            r.setRoleName(rn);
            probe.setRole(r);
        }

        // ---- Matcher ----
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("gender", m -> m.ignoreCase())
                .withMatcher("role.roleName", m -> m.ignoreCase())
                .withIgnorePaths(
                        "userId","fullName","birthDate","email","password","phoneNumber","avatarUrl",
                        "roleId","createdAt","updateAt","bookshelve","cart","wallet"
                )
                .withIgnoreNullValues();

        Example<User> example = Example.of(probe, matcher);

        // ---- Query ----
        return userRepository.findAll(example, pageable)
                .map(mapper::toResponse);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

}