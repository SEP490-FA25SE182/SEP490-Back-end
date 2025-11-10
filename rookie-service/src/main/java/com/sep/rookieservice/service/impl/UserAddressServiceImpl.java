package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.UserAddressRequest;
import com.sep.rookieservice.dto.UserAddressResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.entity.UserAddress;
import com.sep.rookieservice.mapper.UserAddressMapper;
import com.sep.rookieservice.repository.UserAddressRepository;
import com.sep.rookieservice.service.UserAddressService;
import lombok.RequiredArgsConstructor;
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
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserAddressMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allUserAddresses", key = "'all'")
    public List<UserAddressResponse> getAll() {
        return userAddressRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "UserAddress", key = "#id")
    public UserAddressResponse getById(String id) {
        var address = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserAddress not found: " + id));
        return mapper.toResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getByUserId(String userId) {
        return userAddressRepository
                .findByUserIdAndIsActived(userId, IsActived.ACTIVE)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = {"allUserAddresses", "UserAddress"}, allEntries = true)
    public List<UserAddressResponse> create(List<UserAddressRequest> requests) {

        Map<String, List<UserAddressRequest>> byUser =
                requests.stream().collect(Collectors.groupingBy(UserAddressRequest::getUserId));

        byUser.forEach((userId, list) -> {
            Predicate<UserAddressRequest> isActiveReq =
                    r -> r.getIsActived() == null || r.getIsActived() == IsActived.ACTIVE;

            long defaultsActiveInReq = list.stream()
                    .filter(r -> Boolean.TRUE.equals(r.isDefault()) && isActiveReq.test(r))
                    .count();

            if (defaultsActiveInReq > 1) {
                throw new IllegalArgumentException("Mỗi user chỉ được 1 địa chỉ mặc định đang ACTIVE.");
            }

            if (defaultsActiveInReq == 1) {
                // Có đúng 1 default ACTIVE trong batch -> dọn default ACTIVE cũ ở DB
                clearActiveDefaultForUser(userId);
            } else {
                boolean hasActiveDefaultInDb =
                        userAddressRepository.countByUserIdAndIsDefaultTrueAndIsActived(userId, IsActived.ACTIVE) > 0;

                if (!hasActiveDefaultInDb) {
                    list.stream().filter(isActiveReq).findFirst().ifPresent(firstActive -> firstActive.setDefault(true));
                }
            }
        });

        var entities = requests.stream().map(req -> {
            var ua = new UserAddress();
            mapper.copyForCreate(req, ua);

            // Mặc định ACTIVE nếu null
            if (ua.getIsActived() == null) ua.setIsActived(IsActived.ACTIVE);

            if (ua.getCreatedAt() == null) ua.setCreatedAt(Instant.now());
            ua.setUpdatedAt(Instant.now());
            return ua;
        }).toList();

        return userAddressRepository.saveAll(entities)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allUserAddresses", "UserAddress"}, allEntries = true)
    public UserAddressResponse update(String id, UserAddressRequest request) {
        var address = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserAddress not found: " + id));

        mapper.copyForUpdate(request, address);
        address.setUpdatedAt(Instant.now());

        return mapper.toResponse(userAddressRepository.save(address));
    }

    @Override
    @CacheEvict(value = {"allUserAddresses", "UserAddress"}, allEntries = true)
    public void softDelete(String id) {
        var address = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserAddress not found: " + id));
        address.setIsActived(IsActived.INACTIVE);
        address.setUpdatedAt(Instant.now());
        userAddressRepository.save(address);
    }

    @Override
    public Page<UserAddressResponse> search(
            IsActived isActived,
            String phoneNumber,
            String type,
            String userId,
            Boolean isDefault,
            Pageable pageable
    ) {
        UserAddress probe = new UserAddress();

        if (isActived != null) probe.setIsActived(isActived);
        if (phoneNumber != null && !phoneNumber.isBlank()) probe.setPhoneNumber(phoneNumber.trim());
        if (type != null && !type.isBlank()) probe.setType(type.trim());
        if (userId != null && !userId.isBlank()) probe.setUserId(userId.trim());
        if (isDefault != null) probe.setDefault(isDefault);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnorePaths(
                        "userAddressId", "addressInfor",
                        "createdAt", "updatedAt", "user"
                )
                .withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.exact());

        if (isDefault == null) {
            matcher = matcher.withIgnorePaths("isDefault");
        }

        Example<UserAddress> example = Example.of(probe, matcher);

        return userAddressRepository.findAll(example, pageable)
                .map(mapper::toResponse);
    }

    private void clearActiveDefaultForUser(String userId) {
        List<UserAddress> currentActiveDefaults =
                userAddressRepository.findAllByUserIdAndIsDefaultTrueAndIsActived(userId, IsActived.ACTIVE);
        if (!currentActiveDefaults.isEmpty()) {
            currentActiveDefaults.forEach(ua -> ua.setDefault(false));
            userAddressRepository.saveAll(currentActiveDefaults);
        }
    }
}

