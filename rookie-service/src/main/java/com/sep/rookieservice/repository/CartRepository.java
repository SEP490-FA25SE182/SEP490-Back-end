package com.sep.rookieservice.repository;

import com.sep.rookieservice.model.Cart;
import com.sep.rookieservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
}