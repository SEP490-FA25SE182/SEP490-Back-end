package com.sep.rookieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.rookieservice.enums.IsActived;
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
@Table(name = "carts")
public class Cart implements Serializable {
    @Id
    @Column(name = "cart_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cartId;

    @Column(name = "amount")
    private int amount;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @NotNull
    @Column(name = "user_id", length = 50)
    private String userId;

    //OneToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true, insertable = false, updatable = false)
    private User user;

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;

    @JsonIgnore
    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    private List<Order> orders;
}