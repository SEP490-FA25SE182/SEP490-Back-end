package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Book;
import com.sep.rookieservice.entity.Order;
import com.sep.rookieservice.entity.OrderDetail;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PurchasedBookSpecification {

    private PurchasedBookSpecification() {}

    public static Specification<Book> forUserAndStatus(String userId, List<Byte> statuses, String q) {
        return (root, query, cb) -> {
            // Subquery: find books that appear in completed/delivered orders for this user
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Order> orderRoot = subquery.from(Order.class);
            Join<Order, OrderDetail> odJoin = orderRoot.join("orderDetails");
            Join<OrderDetail, Book> bookJoin = odJoin.join("book");

            List<Predicate> subPredicates = new ArrayList<>();
            subPredicates.add(cb.equal(orderRoot.get("wallet").get("userId"), userId));
            subPredicates.add(orderRoot.get("status").in(statuses));
            subPredicates.add(cb.equal(bookJoin.get("bookId"), root.get("bookId")));

            subquery.select(bookJoin.get("bookId"))
                    .where(subPredicates.toArray(new Predicate[0]));

            // Main predicates
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.exists(subquery));

            if (q != null && !q.trim().isEmpty()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("bookName")), pattern));
            }

            // Important: For SQL Server + DISTINCT + ORDER BY
            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}