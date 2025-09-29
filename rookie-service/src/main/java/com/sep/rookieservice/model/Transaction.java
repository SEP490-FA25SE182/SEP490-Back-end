package com.sep.rookieservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.rookieservice.enums.BookEnum;
import com.sep.rookieservice.enums.TransactionEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction implements Serializable {
    @Id
    @Column(name = "transaction_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "status")
    private Byte status = TransactionEnum.NOT_PAID.getStatus();

    @NotNull
    @Column(name = "payment_method_id", length = 50, insertable = false, updatable = false)
    private String walletId;

    @NotNull
    @Column(name = "order_id", length = 50, insertable = false, updatable = false)
    private String orderId;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", referencedColumnName = "payment_method_id", insertable = false, updatable = false)
    private PaymentMethod paymentMethod;

    //OneToOne
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false, unique = true)
    private Order order;
}