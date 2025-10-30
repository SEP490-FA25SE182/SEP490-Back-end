package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.config.PayOSProperties;
import com.sep.rookieservice.dto.CreateCheckoutResponse;
import com.sep.rookieservice.dto.PayOSCreateLinkRequest;
import com.sep.rookieservice.dto.PayOSCreateLinkResponse;
import com.sep.rookieservice.dto.WebhookPayload;
import com.sep.rookieservice.entity.Order;
import com.sep.rookieservice.entity.PaymentMethod;
import com.sep.rookieservice.entity.Transaction;
import com.sep.rookieservice.entity.Wallet;
import com.sep.rookieservice.enums.OrderEnum;
import com.sep.rookieservice.enums.TransactionEnum;
import com.sep.rookieservice.gateway.PayOSClient;
import com.sep.rookieservice.repository.OrderRepository;
import com.sep.rookieservice.repository.PaymentMethodRepository;
import com.sep.rookieservice.repository.TransactionRepository;
import com.sep.rookieservice.repository.WalletRepository;
import com.sep.rookieservice.service.PaymentService;
import com.sep.rookieservice.util.PayOSSignature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.zip.CRC32;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepo;
    private final TransactionRepository txRepo;
    private final PaymentMethodRepository pmRepo;
    private final WalletRepository walletRepo;
    private final PayOSClient payOSClient;
    private final PayOSProperties props;

    private static final String PAYOS_METHOD_NAME = "PayOS";
    private static final String PAYOS_PROVIDER   = "PayOS";

    @Override
    public CreateCheckoutResponse createCheckout(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        long orderCode = generateOrderCodeFromOrderId(orderId);

        int amount = (int) Math.round(order.getTotalPrice());
        order.setAmount(amount);
        order.setUpdatedAt(Instant.now());

        // Description: ngắn gọn, không parse lại
        String description = buildDescription(orderId, orderCode);

        // Gắn orderId vào returnUrl để FE có thể đọc trực tiếp
        String returnUrl = props.getReturnUrl();
        String returnUrlWithOid = returnUrl.contains("?")
                ? returnUrl + "&orderId=" + orderId
                : returnUrl + "?orderId=" + orderId;

        String dataStr = PayOSSignature.buildCreateLinkDataString(
                amount, props.getCancelUrl(), description, orderCode, returnUrlWithOid
        );
        String signature = PayOSSignature.hmacSha256(props.getChecksumKey(), dataStr);

        PayOSCreateLinkRequest req = PayOSCreateLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .cancelUrl(props.getCancelUrl())
                .returnUrl(returnUrlWithOid)
                .signature(signature)
                .build();

        PayOSCreateLinkResponse res = payOSClient.createPaymentLink(req);
        if (!"00".equals(res.getCode())) {
            throw new IllegalStateException("Create payment link failed: " + res.getDesc());
        }

        // Ensure PaymentMethod "PayOS"
        String pmId = pmRepo.findByMethodNameIgnoreCase(PAYOS_METHOD_NAME)
                .or(() -> pmRepo.findByProviderIgnoreCase(PAYOS_PROVIDER))
                .orElseGet(() -> {
                    PaymentMethod pm = new PaymentMethod();
                    pm.setMethodName(PAYOS_METHOD_NAME);
                    pm.setProvider(PAYOS_PROVIDER);
                    pm.setDecription("Thanh toán qua PayOS VietQR");
                    return pmRepo.save(pm);
                }).getPaymentMethodId();

        // Tạo/ cập nhật Transaction (PROCESSING)
        Transaction tx = txRepo.findByOrderId(orderId).orElseGet(Transaction::new);
        tx.setOrderId(orderId);
        tx.setOrder(order);
        tx.setPaymentMethodId(pmId);
        tx.setTotalPrice(order.getTotalPrice());
        tx.setStatus(TransactionEnum.PROCESSING.getStatus());
        tx.setOrderCode(orderCode);
        tx.setUpdatedAt(Instant.now());
        txRepo.save(tx);

        // Cập nhật Order -> PROCESSING
        order.setStatus(OrderEnum.PROCESSING.getStatus());
        orderRepo.save(order);

        return CreateCheckoutResponse.builder()
                .checkoutUrl(res.getData().getCheckoutUrl())
                .qrCode(res.getData().getQrCode())
                .paymentLinkId(res.getData().getPaymentLinkId())
                .orderCode(res.getData().getOrderCode())
                .amount(res.getData().getAmount())
                .build();
    }

    @Override
    public void handleWebhook(WebhookPayload payload) {
        String dataString = PayOSSignature.buildWebhookDataString(payload.getData());
        String expected = PayOSSignature.hmacSha256(props.getChecksumKey(), dataString);
        if (!expected.equals(payload.getSignature())) {
            throw new SecurityException("Invalid webhook signature");
        }

        Object codeObj = payload.getData().get("code");          // "00" nếu thành công
        Object orderCodeObj = payload.getData().get("orderCode"); // số long PayOS gửi
        if (orderCodeObj == null) return;

        long orderCode = Long.parseLong(orderCodeObj.toString());

        // Tra trực tiếp Transaction theo orderCode
        Transaction tx = txRepo.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalStateException("Transaction not found by orderCode"));

        Order order = orderRepo.findById(tx.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order not found by orderId"));

        String code = String.valueOf(codeObj);

        if ("00".equals(code)) {
            if (!TransactionEnum.PAID.equals(TransactionEnum.getByStatus(tx.getStatus()))) {
                tx.setStatus(TransactionEnum.PAID.getStatus());
                tx.setUpdatedAt(Instant.now());
                tx.setOrderCode(orderCode);
                txRepo.save(tx);

                // Order sang PROCESSING
                order.setStatus(OrderEnum.PROCESSING.getStatus());
                order.setUpdatedAt(Instant.now());
                orderRepo.save(order);

                // Cộng coin 1%
                Wallet w = order.getWallet();
                if (w != null) {
                    int bonus = (int) Math.floor(order.getTotalPrice() * 0.01d);
                    w.setCoin(w.getCoin() + bonus);
                    w.setUpdatedAt(Instant.now());
                    walletRepo.save(w);
                }
            }
        } else {
            tx.setStatus(TransactionEnum.CANCELED.getStatus());
            tx.setUpdatedAt(Instant.now());
            tx.setOrderCode(orderCode);
            txRepo.save(tx);

            order.setStatus(OrderEnum.CANCELLED.getStatus());
            order.setUpdatedAt(Instant.now());
            orderRepo.save(order);
        }
    }


    /**
     * Tạo orderCode 10 chữ số (>= 1_000_000_000) từ UUID:
     * - Dùng CRC32 để có int dương ổn định
     * - Map về [1_000_000_000 .. 9_999_999_999] để phù hợp giới hạn số
     */
    private long generateOrderCodeFromOrderId(String orderId) {
        CRC32 crc = new CRC32();
        crc.update(orderId.getBytes(StandardCharsets.UTF_8));
        long x = crc.getValue(); // 0..2^32-1
        return (x % 9_000_000_000L) + 1_000_000_000L;
    }

    private static String maxLen(String s, int max) {
        return (s == null || s.length() <= max) ? s : s.substring(0, max);
    }

    private static String buildDescription(String orderId, long orderCode) {
        String shortOid = (orderId != null && orderId.length() >= 8)
                ? orderId.substring(0, 8)
                : String.valueOf(orderId);

        // Lấy 4 số cuối của orderCode
        String ocStr = String.valueOf(orderCode);
        String lastOc = ocStr.length() > 4 ? ocStr.substring(ocStr.length() - 4) : ocStr;

        // Ví dụ: "OID=9f1a2c3d OC=7421" (≤ 25 ký tự)
        String desc = "OID=" + shortOid + " OC=" + lastOc;
        return maxLen(desc, 25);
    }

}

