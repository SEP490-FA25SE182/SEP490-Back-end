package com.sep.rookieservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "wallets")
public class Wallet implements Serializable {
    @Id
    @Column(name = "wallet_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String walletId;

    @NotNull
    @Column(name = "coin")
    private int coin;

    @NotNull
    @Column(name = "user_id", length = 50, insertable = false, updatable = false)
    private String userId;

    @Column(name = "updated_at", updatable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    //OneToOne
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private User user;

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Order> orders;
}