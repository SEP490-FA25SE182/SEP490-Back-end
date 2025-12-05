package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.config.PayOSProperties;
import com.sep.rookieservice.dto.*;
import com.sep.rookieservice.entity.Order;
import com.sep.rookieservice.entity.PaymentMethod;
import com.sep.rookieservice.entity.Transaction;
import com.sep.rookieservice.entity.Wallet;
import com.sep.rookieservice.enums.BankBin;
import com.sep.rookieservice.enums.OrderEnum;
import com.sep.rookieservice.enums.TransactionEnum;
import com.sep.rookieservice.enums.TransactionType;
import com.sep.rookieservice.gateway.PayOSClient;
import com.sep.rookieservice.repository.OrderRepository;
import com.sep.rookieservice.repository.PaymentMethodRepository;
import com.sep.rookieservice.repository.TransactionRepository;
import com.sep.rookieservice.repository.WalletRepository;
import com.sep.rookieservice.service.PaymentService;
import com.sep.rookieservice.util.PayOSSignature;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.zip.CRC32;

import static org.apache.http.util.TextUtils.isBlank;

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

    @Qualifier("payOSPayoutWebClient")
    private final WebClient payOSPayoutWebClient;

    @Override
    @Transactional
    public CreateCheckoutResponse createCheckout(String orderId, String returnUrl, String cancelUrl) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        long orderCode = generateOrderCodeFromOrderId(orderId);

        int amount = (int) Math.round(order.getTotalPrice());
        order.setTotalPrice(amount);
        order.setUpdatedAt(Instant.now());

        String description = buildDescription(orderId, orderCode);

        // nếu client không truyền thì dùng default từ application.yml
        String finalReturnUrl = isBlank(returnUrl) ? props.getReturnUrl() : returnUrl;
        String finalCancelUrl = isBlank(cancelUrl) ? props.getCancelUrl() : cancelUrl;

        // thêm orderId vào returnUrl để FE đọc trực tiếp
        String returnUrlWithOid = finalReturnUrl.contains("?")
                ? finalReturnUrl + "&orderId=" + orderId
                : finalReturnUrl + "?orderId=" + orderId;

        String dataStr = PayOSSignature.buildCreateLinkDataString(
                amount, finalCancelUrl, description, orderCode, returnUrlWithOid
        );
        String signature = PayOSSignature.hmacSha256(props.getChecksumKey(), dataStr);

        PayOSCreateLinkRequest req = PayOSCreateLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .cancelUrl(finalCancelUrl)
                .returnUrl(returnUrlWithOid)
                .signature(signature)
                .build();

        PayOSCreateLinkResponse res = payOSClient.createPaymentLink(req);
        if (!"00".equals(res.getCode())) {
            throw new IllegalStateException("Create payment link failed: " + res.getDesc());
        }

        String pmId = pmEnsurePayOS();

        Transaction tx = txRepo.findByOrderId(orderId).orElseGet(Transaction::new);
        tx.setOrderId(orderId);
        tx.setOrder(order);
        tx.setPaymentMethodId(pmId);
        tx.setTotalPrice(order.getTotalPrice());
        tx.setStatus(TransactionEnum.PROCESSING.getStatus());
        tx.setOrderCode(orderCode);
        tx.setUpdatedAt(Instant.now());
        tx.setTransType(TransactionType.PAYMENT);
        txRepo.save(tx);

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
    @Transactional
    public void handleWebhook(@RequestBody Map<String, Object> payload) {  // ← Đổi thành Map luôn
        try {
        // 1. Lấy data và signature
        Object dataObj = payload.get("data");
        String receivedSignature = (String) payload.get("signature");

        if (dataObj == null || receivedSignature == null) {
            // Log để debug sau này
            System.out.println("Invalid webhook format: missing data or signature");
            return;
        }

        // PayOS gửi data dưới dạng Map hoặc LinkedHashMap
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) dataObj;

        // 2. Tính lại signature đúng chuẩn PayOS
        String dataString = PayOSSignature.buildWebhookDataString(data);
        String expectedSignature = PayOSSignature.hmacSha256(props.getChecksumKey(), dataString);

        if (!expectedSignature.equals(receivedSignature)) {
            System.out.println("Invalid signature! Expected: " + expectedSignature + ", Got: " + receivedSignature);
            throw new SecurityException("Invalid webhook signature");
        }

        // 3. Lấy orderCode – bắt buộc phải có
        Object orderCodeObj = data.get("orderCode");
        if (orderCodeObj == null) {
            System.out.println("Webhook missing orderCode: " + data);
            return;
        }

        long orderCode = Long.parseLong(orderCodeObj.toString());
        String statusCode = String.valueOf(data.getOrDefault("code", ""));

        // 4. Tìm transaction
        Transaction tx = txRepo.findByOrderCode(orderCode).orElse(null);
        if (tx == null) {
            System.out.println("Transaction not found for orderCode: " + orderCode);
            return;
        }

        // 5. Chỉ xử lý khi là thanh toán thành công
        if ("00".equals(statusCode)) {
            if (tx.getStatus() == null || tx.getStatus() != TransactionEnum.PAID.getStatus()) {
                tx.setStatus(TransactionEnum.PAID.getStatus());
                tx.setUpdatedAt(Instant.now());
                txRepo.save(tx);

                // Xử lý cộng tiền ví / bonus coin (giữ nguyên code cũ của bạn)
                handlePaymentSuccess(tx, data);
            }
        } else {
            // Hủy hoặc thất bại
            tx.setStatus(TransactionEnum.CANCELED.getStatus());
            tx.setUpdatedAt(Instant.now());
            txRepo.save(tx);

            if (tx.getTransType() == TransactionType.PAYMENT && tx.getOrderId() != null) {
                Order order = orderRepo.findById(tx.getOrderId()).orElse(null);
                if (order != null) {
                    order.setStatus(OrderEnum.CANCELLED.getStatus());
                    order.setUpdatedAt(Instant.now());
                    orderRepo.save(order);
                }
            }
        }
        } catch (Exception e) {
        // In ra log để biết chính xác lỗi gì
        e.printStackTrace();
    }
    }

    // Tách riêng để code sạch hơn
    private void handlePaymentSuccess(Transaction tx, Map<String, Object> data) {
        Object amountObj = data.get("amount");
        int amount = amountObj != null ? Integer.parseInt(amountObj.toString()) : (int) Math.round(tx.getTotalPrice());

        if (tx.getTransType() == TransactionType.PAYMENT && tx.getOrderId() != null) {
            Order order = orderRepo.findById(tx.getOrderId()).orElse(null);
            if (order != null) {
                order.setStatus(OrderEnum.PROCESSING.getStatus());
                order.setUpdatedAt(Instant.now());
                orderRepo.save(order);

                Wallet w = order.getWallet();
                if (w != null) {
                    int bonus = (int) Math.floor(order.getTotalPrice() * 0.01d);
                    w.setCoin(w.getCoin() + bonus);
                    w.setUpdatedAt(Instant.now());
                    walletRepo.save(w);
                }
            }
        } else if (tx.getTransType() == TransactionType.DEPOSIT && tx.getWalletId() != null) {
            Wallet w = walletRepo.findById(tx.getWalletId()).orElse(null);
            if (w != null) {
                w.setBalance(w.getBalance() + amount);
                w.setUpdatedAt(Instant.now());
                walletRepo.save(w);
            }
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

    @Override
    @Transactional
    public CreateCheckoutResponse deposit(int amount, String walletId, String returnUrl, String cancelUrl) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");

        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        long orderCode = generateOrderCodeFromOrderId("WALLET:" + walletId + ":" + Instant.now().toEpochMilli());
        String desc = buildDepositDescription(walletId, orderCode);

        String finalReturnUrl = isBlank(returnUrl) ? props.getReturnUrl() : returnUrl;
        String finalCancelUrl = isBlank(cancelUrl) ? props.getCancelUrl() : cancelUrl;

        String returnUrlWithType = finalReturnUrl + (finalReturnUrl.contains("?") ? "&" : "?")
                + "type=DEPOSIT&orderCode=" + orderCode;

        String dataStr = PayOSSignature.buildCreateLinkDataString(
                amount, finalCancelUrl, desc, orderCode, returnUrlWithType
        );
        String signature = PayOSSignature.hmacSha256(props.getChecksumKey(), dataStr);

        PayOSCreateLinkRequest req = PayOSCreateLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(desc)
                .cancelUrl(finalCancelUrl)
                .returnUrl(returnUrlWithType)
                .signature(signature)
                .build();

        PayOSCreateLinkResponse res = payOSClient.createPaymentLink(req);
        if (!"00".equals(res.getCode())) {
            throw new IllegalStateException("Create payment link failed: " + res.getDesc());
        }

        String pmId = pmEnsurePayOS();

        // Tạo Transaction làm “intent” DEPOSIT
        Transaction tx = new Transaction();
        tx.setPaymentMethodId(pmId);
        tx.setTotalPrice(amount);
        tx.setStatus(TransactionEnum.PROCESSING.getStatus());
        tx.setOrderCode(orderCode);
        tx.setUpdatedAt(Instant.now());
        tx.setTransType(TransactionType.DEPOSIT);
        tx.setWalletId(walletId);
        txRepo.save(tx);

        return CreateCheckoutResponse.builder()
                .checkoutUrl(res.getData().getCheckoutUrl())
                .qrCode(res.getData().getQrCode())
                .paymentLinkId(res.getData().getPaymentLinkId())
                .orderCode(res.getData().getOrderCode())
                .amount(res.getData().getAmount())
                .build();
    }

    private static String buildDepositDescription(String walletId, long orderCode) {
        String shortWid = (walletId != null && walletId.length() >= 8) ? walletId.substring(0, 8) : String.valueOf(walletId);
        String ocStr = String.valueOf(orderCode);
        String lastOc = ocStr.length() > 4 ? ocStr.substring(ocStr.length() - 4) : ocStr;
        return maxLen("DEP WAL=" + shortWid + " OC=" + lastOc, 25);
    }


    private String pmEnsurePayOS() {
        return pmRepo.findByMethodNameIgnoreCase(PAYOS_METHOD_NAME)
                .or(() -> pmRepo.findByProviderIgnoreCase(PAYOS_PROVIDER))
                .orElseGet(() -> {
                    PaymentMethod pm = new PaymentMethod();
                    pm.setMethodName(PAYOS_METHOD_NAME);
                    pm.setProvider(PAYOS_PROVIDER);
                    pm.setDecription("Thanh toán qua PayOS VietQR");
                    return pmRepo.save(pm);
                }).getPaymentMethodId();
    }

    // Utils: giữ ASCII, cắt đúng 25 ký tự
    static String shortenDesc(String s, int maxLen) {
        if (s == null) return "";
        s = s.trim();
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen);
    }

    static String buildWithdrawDesc(String walletId, String accountNumber) {
        String w = walletId == null ? "" : walletId.replaceAll("-", "");
        String last4 = (accountNumber != null && accountNumber.length() >= 4)
                ? accountNumber.substring(accountNumber.length() - 4) : "";
        String base = "WDR " + (w.length() >= 8 ? w.substring(0, 8) : w) + " " + last4;
        return shortenDesc(base, 25);
    }

    @Override
    @Transactional
    public CreateCheckoutResponse withdraw(
            int amount, String walletId, String accountName,
            String bankName, String accountNumber,
            String returnUrl, String cancelUrl
    ) {
        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        if (wallet.getBalance() < amount) throw new IllegalStateException("Insufficient balance");

        String toBin = BankBin.resolveBin(bankName);
        if (toBin == null || toBin.isBlank())
            throw new IllegalArgumentException("Unsupported bankName; please provide BIN");

        if (props.getPayoutChecksumKey() == null || props.getPayoutChecksumKey().isBlank())
            throw new IllegalStateException("Missing rookie.payos.payout-checksum-key");
        if (props.getPayoutChecksumKey().equals(props.getChecksumKey()))
            System.out.println("[WARN] payoutChecksumKey equals payment checksumKey – xác nhận lại trên PayOS Dashboard!");

        String referenceId = "WDR-" + walletId + "-" + System.currentTimeMillis();

        String description = buildWithdrawDesc(walletId, accountNumber);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("referenceId", referenceId);
        body.put("amount", amount);
        body.put("description", description);
        body.put("toBin", toBin);
        body.put("toAccountNumber", accountNumber);

        String checksum = props.getPayoutChecksumKey().trim();

        String canonical = PayOSSignature.canonical5(
                amount, description, referenceId, accountNumber, toBin, true);

        String signature = PayOSSignature.hmacSha256(checksum, canonical);

        System.out.println("[PAYOUT] canonical=" + canonical);
        System.out.println("[PAYOUT] signature=" + signature);

        return callPayoutAndPersist(signature, body, walletId, amount, referenceId);


    }

    private CreateCheckoutResponse callPayoutAndPersist(
            String signature,
            Map<String, Object> body,
            String walletId,
            int amount,
            String referenceId
    ) {
        // Gọi PayOS
        PayOSPayoutResponse res = payOSPayoutWebClient.post()
                .uri(props.getCreatePayoutPath())
                .header("x-idempotency-key", UUID.randomUUID().toString())
                .header("x-signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.bodyToMono(String.class)
                        .map(msg -> new IllegalStateException("PayOS error " + r.statusCode() + ": " + msg)))
                .bodyToMono(PayOSPayoutResponse.class)
                .block();

        if (res == null || !"00".equals(res.getCode())) {
            throw new IllegalStateException("Create payout failed: " + (res != null ? res.getDesc() : "null response"));
        }

        // persist transaction intent
        String pmId = pmEnsurePayOS();
        long orderCode = generateOrderCodeFromOrderId(referenceId);

        Transaction tx = new Transaction();
        tx.setPaymentMethodId(pmId);
        tx.setTotalPrice(amount);
        tx.setStatus(TransactionEnum.PROCESSING.getStatus());
        tx.setOrderCode(orderCode);
        tx.setUpdatedAt(Instant.now());
        tx.setTransType(TransactionType.WITHDRAW);
        tx.setWalletId(walletId);
        txRepo.save(tx);

        Wallet w = walletRepo.findById(walletId)
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));

        BigDecimal before = BigDecimal.valueOf(w.getBalance());
        BigDecimal afterBD = before.subtract(BigDecimal.valueOf(amount));
        if (afterBD.signum() < 0) throw new IllegalStateException("Insufficient balance");

        double after = afterBD.doubleValue();
        w.setBalance(after);
        w.setUpdatedAt(Instant.now());
        walletRepo.save(w);

        return CreateCheckoutResponse.builder()
                .checkoutUrl(res.getData() != null ? res.getData().getApprovalUrl() : null)
                .qrCode(null)
                .paymentLinkId(res.getData() != null ? res.getData().getPayoutId() : null)
                .orderCode(orderCode)
                .amount(amount)
                .build();
    }
}

