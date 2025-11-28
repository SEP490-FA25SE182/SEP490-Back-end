package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.TransactionRequest;
import com.sep.rookieservice.dto.TransactionResponse;
import com.sep.rookieservice.entity.PaymentMethod;
import com.sep.rookieservice.entity.Transaction;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.TransactionEnum;
import com.sep.rookieservice.enums.TransactionType;
import com.sep.rookieservice.mapper.TransactionMapper;
import com.sep.rookieservice.repository.PaymentMethodRepository;
import com.sep.rookieservice.repository.TransactionRepository;
import com.sep.rookieservice.service.TransactionService;
import com.sep.rookieservice.util.TransactionQbe;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final TransactionMapper mapper;

    @Override
    //@CacheEvict(value = {"allTransactions","Transaction"}, allEntries = true)
    public TransactionResponse create(TransactionRequest req) {
        TransactionEnum.getByStatus(req.getStatus());

        if (req.getOrderCode() != null && repository.existsByOrderCode(req.getOrderCode())) {
            throw new IllegalArgumentException("orderCode đã tồn tại");
        }

        Transaction e = new Transaction();
        mapper.copyForCreate(req, e);
        if (e.getCreatedAt() == null) e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());

        return mapper.toResponse(repository.save(e));
    }

    @CacheEvict(value = {"allTransactions","Transaction"}, allEntries = true)
    public TransactionResponse createWallet(TransactionRequest req) {
        TransactionEnum.getByStatus(req.getStatus());

        // validate orderCode
        if (req.getOrderCode() != null && repository.existsByOrderCode(req.getOrderCode())) {
            throw new IllegalArgumentException("orderCode đã tồn tại");
        }

        PaymentMethod pm = findActivePaymentMethod("Rookies", "Rookies");

        Transaction e = new Transaction();
        mapper.copyForCreate(req, e);
        e.setPaymentMethodId(pm.getPaymentMethodId());
        if (e.getCreatedAt() == null) e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());

        return mapper.toResponse(repository.save(e));
    }

    @CacheEvict(value = {"allTransactions","Transaction"}, allEntries = true)
    public TransactionResponse createCOD(TransactionRequest req) {
        TransactionEnum.getByStatus(req.getStatus());

        // validate orderCode
        if (req.getOrderCode() != null && repository.existsByOrderCode(req.getOrderCode())) {
            throw new IllegalArgumentException("orderCode đã tồn tại");
        }

        PaymentMethod pm = findActivePaymentMethod("COD", "COD");

        Transaction e = new Transaction();
        e.setTransType(TransactionType.PAYMENT);
        mapper.copyForCreate(req, e);
        e.setPaymentMethodId(pm.getPaymentMethodId());
        if (e.getCreatedAt() == null) e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());

        return mapper.toResponse(repository.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Transaction", key = "#id")
    public TransactionResponse getById(String id) {
        var e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));
        return mapper.toResponse(e);
    }

    @Override
    @CacheEvict(value = {"allTransactions","Transaction"}, allEntries = true)
    public TransactionResponse update(String id, TransactionRequest req) {
        var e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));

        // validate status hợp lệ
        TransactionEnum.getByStatus(req.getStatus());

        // nếu đổi orderCode thì check unique
        if (req.getOrderCode() != null && !req.getOrderCode().equals(e.getOrderCode())
                && repository.existsByOrderCode(req.getOrderCode())) {
            throw new IllegalArgumentException("orderCode đã tồn tại");
        }

        mapper.copyForUpdate(req, e);
        e.setUpdatedAt(Instant.now());

        return mapper.toResponse(repository.save(e));
    }

    @Override
    @CacheEvict(value = {"allTransactions","Transaction"}, allEntries = true)
    public void softDelete(String id) {
        var e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));
        e.setIsActived(IsActived.INACTIVE);
        e.setUpdatedAt(Instant.now());
        repository.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> search(
            TransactionEnum status,
            IsActived isActived,
            String paymentMethodName,
            String orderId,
            String paymentMethodId,
            TransactionType transType,
            String walletId,
            Pageable pageable
    ) {
        var example = TransactionQbe.example(
                status, isActived, paymentMethodName, orderId, paymentMethodId, transType, walletId
        );

        // nếu client không truyền sort → mặc định updatedAt DESC
        Pageable sorted = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "updatedAt"));

        return repository.findAll(example, sorted).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    protected PaymentMethod findActivePaymentMethod(String methodName, String provider) {
        PaymentMethod probe = new PaymentMethod();
        probe.setMethodName(methodName);
        probe.setProvider(provider);
        probe.setIsActived(IsActived.ACTIVE);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withIgnorePaths("paymentMethodId", "createdAt", "updatedAt", "decription")
                .withMatcher("methodName", m -> m.ignoreCase())
                .withMatcher("provider", m -> m.ignoreCase());

        return paymentMethodRepository.findOne(Example.of(probe, matcher))
                .orElseThrow(() -> new IllegalArgumentException(
                        "PaymentMethod không tồn tại hoặc không ACTIVE: methodName=" + methodName + ", provider=" + provider));
    }

}
