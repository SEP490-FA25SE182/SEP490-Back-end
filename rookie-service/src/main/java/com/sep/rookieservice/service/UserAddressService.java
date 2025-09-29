package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.UserAddressDto;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.model.UserAddress;
import com.sep.rookieservice.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAddressService {
    private final UserAddressRepository userAddressRepository;

    @Cacheable(value = "allUserAddresses", key = "'all'")
    public List<UserAddress> getAllUserAddresses() {
        System.out.println("⏳ Querying DB...");
        return userAddressRepository.findAll();
    }

    @CacheEvict(value = "allUserAddresses", allEntries = true)
    public List<UserAddress> createUserAddresses(List<UserAddress> addresses) {
        return userAddressRepository.saveAll(addresses);
    }

    @Cacheable(value = "UserAddress", key = "'id'")
    public Optional<UserAddress> findById(String id) {
        System.out.println("⏳ Querying UserAddress by id...");
        return userAddressRepository.findById(id);
    }

    @CacheEvict(value = {"allUserAddresses", "UserAddress"}, allEntries = true)
    public UserAddress updateUserAddress(String id, UserAddressDto dto) {
        UserAddress address = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserAddress not found with id: " + id));

        address.setAddressInfor(dto.getAddressInfor());
        address.setIsActived(dto.getIsActived());
        address.setUpdatedAt(Instant.now());

        return userAddressRepository.save(address);
    }

    @CacheEvict(value = {"allUserAddresses", "UserAddress"}, allEntries = true)
    public void deleteUserAddress(String id) {
        UserAddress address = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserAddress not found with id: " + id));

        address.setIsActived(IsActived.INACTIVE);
        address.setUpdatedAt(Instant.now());

        userAddressRepository.save(address);
    }
}

