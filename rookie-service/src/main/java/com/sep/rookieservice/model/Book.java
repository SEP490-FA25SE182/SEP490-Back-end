package com.sep.rookieservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.rookieservice.enums.BookEnum;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.PublicationEnum;
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
@Table(name = "books")
public class Book implements Serializable {
    @Id
    @Column(name = "book_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String bookId;

    @Column(name= "book_name", length = 50)
    private String bookName;

    @Column(name = "author_id", length = 50, insertable = false, updatable = false)
    private String authorId;

    @Column(name = "cover_url", length = 100)
    private String coverUrl;

    @Column(name = "decription", length = 250)
    private String decription;

    @Column(name = "progress_status")
    private Byte progressStatus = BookEnum.IN_PROGRESS.getStatus();

    @Column(name = "publication_status")
    private Byte publicationStatus = PublicationEnum.DRAFT.getStatus();

    @Column(name = "bookshelve_id", length = 50, insertable = false, updatable = false)
    private String bookshelveId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @Column(name = "updated_at", updatable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "published_date", updatable = false)
    private Instant publishedDate;

    //@ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User author;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookshelve_id", referencedColumnName = "bookshelve_id", insertable = false, updatable = false)
    private Bookshelve bookshelve;

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;

    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;

    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;

    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Chapter> chapters;

    // ManyToMany
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_genres", // tên bảng join sẽ được tạo ra
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "genre_id")
    )
    private List<Genre> genres;
}