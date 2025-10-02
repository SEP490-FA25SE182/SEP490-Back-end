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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
        return userAddressRepository.findByUserId(userId).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allUserAddresses", "UserAddress"}, allEntries = true)
    public List<UserAddressResponse> create(List<UserAddressRequest> requests) {
        var entities = requests.stream().map(req -> {
            var ua = new UserAddress();
            mapper.copyForCreate(req, ua);
            if (ua.getIsActived() == null) ua.setIsActived(IsActived.ACTIVE);
            if (ua.getCreatedAt() == null) ua.setCreatedAt(Instant.now());
            ua.setUpdatedAt(Instant.now());
            return ua;
        }).toList();

        return userAddressRepository.saveAll(entities).stream().map(mapper::toResponse).toList();
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
}

