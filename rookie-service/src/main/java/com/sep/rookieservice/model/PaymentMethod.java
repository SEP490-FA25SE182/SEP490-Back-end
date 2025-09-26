package com.sep.rookieservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_methods")
public class PaymentMethod implements Serializable {
    @Id
    @Column(name = "payment_method_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String paymentMethodId;

    @Column(name = "method_name", length = 50)
    private String methodName;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "decription", length = 250)
    private String decription;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

}