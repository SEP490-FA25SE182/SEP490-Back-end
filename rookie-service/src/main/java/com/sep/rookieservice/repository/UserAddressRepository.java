package com.sep.rookieservice.repository;

import com.sep.rookieservice.model.Role;
import com.sep.rookieservice.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, String> {
}