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

    public static String n(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static void clearDefaults(Transaction t) {
        t.setStatus(null);
        t.setIsActived(null);
        t.setTransType(null);
        t.setCreatedAt(null);
        t.setUpdatedAt(null);
    }

    public static Transaction buildProbe(
            TransactionEnum status,
            IsActived isActived,
            String paymentMethodName,
            String orderId,
            String paymentMethodId,
            TransactionType transType,
            String walletId
    ) {
        Transaction p = new Transaction();
        clearDefaults(p);

        if (status != null)            p.setStatus(status.getStatus());
        if (isActived != null)         p.setIsActived(isActived);
        if (transType != null)         p.setTransType(transType);
        if (n(orderId) != null)        p.setOrderId(n(orderId));
        if (n(paymentMethodId) != null)p.setPaymentMethodId(n(paymentMethodId));
        if (n(walletId) != null)       p.setWalletId(n(walletId));

        if (n(paymentMethodName) != null) {
            PaymentMethod pm = new PaymentMethod();
            pm.setMethodName(n(paymentMethodName));
            p.setPaymentMethod(pm);
        }
        return p;
    }

    public static ExampleMatcher matcher() {
        return ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withIgnorePaths("transactionId","totalPrice","createdAt","updatedAt","orderCode","order","wallet")
                .withMatcher("orderId", m -> m.exact())
                .withMatcher("paymentMethodId", m -> m.exact())
                .withMatcher("walletId", m -> m.exact())
                .withMatcher("paymentMethod.methodName", m -> m.contains().ignoreCase());
    }

    public static Example<Transaction> example(
            TransactionEnum status,
            IsActived isActived,
            String paymentMethodName,
            String orderId,
            String paymentMethodId,
            TransactionType transType,
            String walletId
    ) {
        Transaction probe = buildProbe(status, isActived, paymentMethodName, orderId, paymentMethodId, transType, walletId);
        return Example.of(probe, matcher());
    }
}


