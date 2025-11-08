package com.sep.rookieservice.util;

import com.sep.rookieservice.entity.PaymentMethod;
import com.sep.rookieservice.entity.Transaction;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.TransactionEnum;
import com.sep.rookieservice.enums.TransactionType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

public final class TransactionQbe {

    private TransactionQbe() {}

    /** Chuẩn hoá chuỗi: trim và trả null nếu rỗng */
    public static String n(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    /** Xoá các default trên entity mới tạo để probe không bị lọc ngoài ý muốn */
    private static void clearDefaults(Transaction t) {
        t.setStatus(null);
        t.setIsActived(null);
        t.setTransType(null);
        t.setCreatedAt(null);
        t.setUpdatedAt(null);
    }

    /** Dựng probe từ các tham số tuỳ chọn */
    public static Transaction buildProbe(
            TransactionEnum status,
            IsActived isActived,
            String paymentMethodName,
            String orderId,
            String paymentMethodId,
            TransactionType transType
    ) {
        Transaction p = new Transaction();
        clearDefaults(p);

        if (status != null)           p.setStatus(status.getStatus());
        if (isActived != null)        p.setIsActived(isActived);
        if (transType != null)        p.setTransType(transType);
        if (n(orderId) != null)       p.setOrderId(n(orderId));
        if (n(paymentMethodId) != null) p.setPaymentMethodId(n(paymentMethodId));

        if (n(paymentMethodName) != null) {
            PaymentMethod pm = new PaymentMethod();
            pm.setMethodName(n(paymentMethodName));
            p.setPaymentMethod(pm);
        }
        return p;
    }

    /** Matcher chuẩn cho Transaction search bằng QBE */
    public static ExampleMatcher matcher() {
        return ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                // bỏ qua cột không dùng để lọc
                .withIgnorePaths("transactionId","totalPrice","createdAt","updatedAt","orderCode","order")
                // ID: exact (tận dụng index)
                .withMatcher("orderId", m -> m.exact())
                .withMatcher("paymentMethodId", m -> m.exact())
                // tên phương thức: contains + ignoreCase
                .withMatcher("paymentMethod.methodName", m -> m.contains().ignoreCase());
    }

    /** Tạo Example<Transaction> từ các tham số */
    public static Example<Transaction> example(
            TransactionEnum status,
            IsActived isActived,
            String paymentMethodName,
            String orderId,
            String paymentMethodId,
            TransactionType transType
    ) {
        Transaction probe = buildProbe(status, isActived, paymentMethodName, orderId, paymentMethodId, transType);
        return Example.of(probe, matcher());
    }
}

