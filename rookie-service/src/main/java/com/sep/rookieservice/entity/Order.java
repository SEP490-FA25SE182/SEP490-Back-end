package com.sep.rookieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.rookieservice.enums.OrderEnum;
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
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @Column(name = "order_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderId;

    @Column(name = "amount")
    private int amount;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "status")
    private Byte status = OrderEnum.UNORDERED.getStatus();

    @NotNull
    @Column(name = "wallet_id", length = 50)
    private String walletId;

    @Column(name = "cart_id", length = 50)
    private String cartId;

    @Column(name = "user_address_id", length = 50)
    private String userAddressId;

    @Column(name = "reason", length = 250)
    private String reason;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", referencedColumnName = "wallet_id", insertable = false, updatable = false)
    private Wallet wallet;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", referencedColumnName = "cart_id", insertable = false, updatable = false)
    private Cart cart;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_address_id", referencedColumnName = "user_address_id", insertable = false, updatable = false)
    private UserAddress userAddress;

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;

    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Transaction> transactions;
}