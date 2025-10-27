package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.UserAddress;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, String> {
    List<UserAddress> findByUserId(String userId);
    List<UserAddress> findByUserIdAndIsActived(String userId, IsActived isActived);

    long countByUserIdAndIsDefaultTrueAndIsActived(String userId, IsActived isActived);
    List<UserAddress> findAllByUserIdAndIsDefaultTrueAndIsActived(String userId, IsActived isActived);
    Optional<UserAddress> findFirstByUserIdAndIsDefaultTrueAndIsActived(String userId, IsActived isActived);
}