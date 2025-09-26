package com.sep.rookieservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart_items")
public class CartItem implements Serializable {
    @Id
    @Column(name = "cart_item_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cartItemId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private double price;

    @NotNull
    @Column(name = "cart_id", length = 50, insertable = false, updatable = false)
    private String cartId;

    @NotNull
    @Column(name = "book_id", length = 50, insertable = false, updatable = false)
    private String bookId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", referencedColumnName = "cart_id", insertable = false, updatable = false)
    private Cart cart;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "book_id", insertable = false, updatable = false)
    private Book book;

}