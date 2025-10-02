package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, String> {
    List<UserAddress> findByUserId(String userId);
}