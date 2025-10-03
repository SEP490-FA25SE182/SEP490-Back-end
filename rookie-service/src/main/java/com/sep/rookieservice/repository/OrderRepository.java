package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByCartId(String cartId);
    List<Order> findByWalletId(String walletId);
}
