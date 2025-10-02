package com.sep.rookieservice.model;

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
@Table(name = "bookshelves")
public class Bookshelve implements Serializable {
    @Id
    @Column(name = "bookshelve_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String bookshelveId;

    @Column(name = "bookshelve_name", length = 50)
    private String bookshelveName;

    @Column(name = "decription", length = 250)
    private String decription;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @NotNull
    @Column(name = "user_id", length = 50, insertable = false, updatable = false)
    private String userId;

    //OneToOne
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "wishlist",joinColumns = @JoinColumn(name = "bookshelve_id", referencedColumnName = "bookshelve_id"), inverseJoinColumns = @JoinColumn(name = "book_id", referencedColumnName = "book_id"))
    private List<Book> books;

}