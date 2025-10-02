package com.sep.rookieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_details")
public class OrderDetail implements Serializable {
    @Id
    @Column(name = "order_detail_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderDetailId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private double price;

    @NotNull
    @Column(name = "order_id", length = 50, insertable = false, updatable = false)
    private String orderId;

    @NotNull
    @Column(name = "book_id", length = 50, insertable = false, updatable = false)
    private String bookId;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false)
    private Order order;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "book_id", insertable = false, updatable = false)
    private Book book;

    //OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "orderDetail", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;
}