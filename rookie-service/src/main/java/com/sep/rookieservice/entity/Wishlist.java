package com.sep.rookieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "wishlist")
public class Wishlist implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wishlist_id", length = 50)
    private String wishlistId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // --- Book ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "book_id", nullable = false)
    @JsonIgnore
    private Book book;

    // --- Bookshelve ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookshelve_id", referencedColumnName = "bookshelve_id", nullable = false)
    @JsonIgnore
    private Bookshelve bookshelve;
}
