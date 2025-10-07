package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.UserAnalyticsResponse;
import com.sep.rookieservice.dto.UserRequest;
import com.sep.rookieservice.dto.UserResponse;
import com.sep.rookieservice.entity.Role;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.entity.User;
import com.sep.rookieservice.mapper.UserMapper;
import com.sep.rookieservice.repository.RoleRepository;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
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
    @Transactional(readOnly = true)
    public Page<UserResponse> search(
            String userId,
            String fullName,
            LocalDate birthDate,
            String gender,
            String email,
            String phoneNumber,
            String roleId,
            IsActived isActived,
            Pageable pageable
    ) {
        String uid = normalize(userId);
        String fn  = normalize(fullName);
        String g   = normalize(gender);
        String em  = normalize(email);
        String pn  = normalize(phoneNumber);
        String rid = normalize(roleId);

        User probe = new User();
        if (uid != null) probe.setUserId(uid);
        if (fn  != null) probe.setFullName(fn);
        if (birthDate != null) probe.setBirthDate(birthDate);
        if (g   != null) probe.setGender(g);
        if (em  != null) probe.setEmail(em);
        if (pn  != null) probe.setPhoneNumber(pn);
        if (rid != null) probe.setRoleId(rid);
        if (isActived != null) probe.setIsActived(isActived);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withMatcher("userId", m -> m.exact())
                .withMatcher("roleId", m -> m.exact())
                .withIgnorePaths(
                        "password", "avatarUrl",
                        "createdAt", "updateAt",
                        "addresses", "books", "feedbacks", "userQuizResults",
                        "bookshelve", "cart", "wallet", "role"
                )
                .withIgnoreNullValues();

        return userRepository.findAll(Example.of(probe, matcher), pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "UserAnalytics", key = "#monthsBack != null ? #monthsBack : 12")
    public UserAnalyticsResponse getAnalytics(Integer monthsBack) {
        int months = (monthsBack == null || monthsBack < 1 || monthsBack > 36) ? 12 : monthsBack;

        long total = userRepository.count();

        List<UserAnalyticsResponse.ActiveCount> byIsActived = Arrays.stream(IsActived.values())
                .map(st -> {
                    long c = userRepository.countByIsActived(st);
                    UserAnalyticsResponse.ActiveCount ac = new UserAnalyticsResponse.ActiveCount();
                    ac.setStatus(st.name());
                    ac.setCount(c);
                    return ac;
                })
                .toList();

        List<Role> roles = roleRepository.findAll();
        List<UserAnalyticsResponse.RoleCount> byRole = new ArrayList<>();
        for (Role r : roles) {
            long c = userRepository.countByRoleId(r.getRoleId());
            UserAnalyticsResponse.RoleCount rc = new UserAnalyticsResponse.RoleCount();
            rc.setRoleId(r.getRoleId());
            rc.setRoleName(r.getRoleName());
            rc.setCount(c);
            byRole.add(rc);
        }

        long noRole = userRepository.countByRoleIdIsNull();
        if (noRole > 0) {
            UserAnalyticsResponse.RoleCount rc = new UserAnalyticsResponse.RoleCount();
            rc.setRoleId(null);
            rc.setRoleName("UNASSIGNED");
            rc.setCount(noRole);
            byRole.add(rc);
        }

        // Đăng ký mới theo tháng (loop từng tháng, không group-by)
        ZoneId tz = ZoneOffset.UTC;
        LocalDate startMonth = LocalDate.now(tz).withDayOfMonth(1).minusMonths(months - 1);

        List<UserAnalyticsResponse.MonthlySignup> monthly = new ArrayList<>(months);
        for (int i = 0; i < months; i++) {
            LocalDate mStart = startMonth.plusMonths(i);
            LocalDate mEnd = mStart.plusMonths(1);

            Instant from = mStart.atStartOfDay(tz).toInstant();
            Instant to = mEnd.atStartOfDay(tz).toInstant();

            long c = userRepository.countByCreatedAtBetween(from, to);

            UserAnalyticsResponse.MonthlySignup ms = new UserAnalyticsResponse.MonthlySignup();
            ms.setYear(mStart.getYear());
            ms.setMonth(mStart.getMonthValue());
            ms.setCount(c);
            monthly.add(ms);
        }

        // Build response
        UserAnalyticsResponse resp = new UserAnalyticsResponse();
        resp.setTotalUsers(total);
        resp.setByRole(byRole);
        resp.setByIsActived(byIsActived);
        resp.setMonthlySignups(monthly);
        return resp;
    }


    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

}