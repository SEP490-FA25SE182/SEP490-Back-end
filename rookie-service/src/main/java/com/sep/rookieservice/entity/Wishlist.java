package com.sep.rookieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "wishlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wishlist_id", length = 50)
    private String wishlistId;

    @Column(name = "user_id", length = 50, insertable = false, updatable = false)
    private String userId;

    @Column(name = "book_id", length = 50, insertable = false, updatable = false)
    private String bookId;

    @Column(name = "bookshelve_id", length = 50, insertable = false, updatable = false)
    private String bookshelfId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "book_id", insertable = false, updatable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookshelve_id", referencedColumnName = "bookshelve_id", insertable = false, updatable = false)
    private Bookshelve bookshelf;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}

