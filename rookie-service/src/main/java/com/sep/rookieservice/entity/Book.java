package com.sep.rookieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.rookieservice.enums.BookEnum;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.PublicationEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "books")
public class Book implements Serializable {

    @Id
    @Column(name = "book_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String bookId;

    @Nationalized
    @Column(name= "book_name", length = 50)
    private String bookName;

    @Column(name = "author_id", length = 50)
    private String authorId;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Nationalized
    @Lob
    @Column(name = "decription")
    private String decription;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Min(0)
    @Column(name = "quantity")
    private Integer quantity = 0;

    @Column(name = "progress_status")
    private Byte progressStatus = BookEnum.IN_PROGRESS.getStatus();

    @Column(name = "publication_status")
    private Byte publicationStatus = PublicationEnum.DRAFT.getStatus();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "published_date")
    private Instant publishedDate;

    // ===================== RELATIONSHIPS =====================

    /** Many Books -> One Author (User) **/
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User author;

    /** One Book -> Many CartItems **/
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    /** One Book -> Many OrderDetails **/
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    /** One Book -> Many Feedbacks **/
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();

    /** One Book -> Many Chapters **/
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Chapter> chapters = new ArrayList<>();

    /** One Book -> Many Blogs **/
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Blog> blogs = new ArrayList<>();

    /**
     * Many-to-Many: Book <-> Genre
     * Join Table: book_genres
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    /**
     * Many-to-Many: Book <-> Bookshelf
     * Join Table: wishlist (acts as linking table)
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "wishlists", // use existing table
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "bookshelve_id", referencedColumnName = "bookshelve_id")
    )
    private List<Bookshelve> bookshelves = new ArrayList<>();
}
