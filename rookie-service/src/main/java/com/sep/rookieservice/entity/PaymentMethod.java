package com.sep.rookieservice.entity;

import com.sep.rookieservice.enums.IsActived;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

}