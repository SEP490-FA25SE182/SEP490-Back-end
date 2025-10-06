package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.User;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    long count();

    long countByIsActived(IsActived status);

    long countByRoleId(String roleId);

    long countByRoleIdIsNull();

    long countByCreatedAtBetween(Instant start, Instant end);
}
