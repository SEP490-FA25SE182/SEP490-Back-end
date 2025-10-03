package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {
    Optional<PaymentMethod> findByProviderIgnoreCase(String provider);
    Optional<PaymentMethod> findByMethodNameIgnoreCase(String methodName);
    boolean existsByMethodNameIgnoreCase(String methodName);
}
