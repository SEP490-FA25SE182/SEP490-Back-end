package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<Transaction> findByOrder_OrderId(String orderId);
    Optional<Transaction> findByOrderId(String orderId);
    boolean existsByOrderCode(Long orderCode);
    Optional<Transaction> findByOrderCode(Long orderCode);
}
