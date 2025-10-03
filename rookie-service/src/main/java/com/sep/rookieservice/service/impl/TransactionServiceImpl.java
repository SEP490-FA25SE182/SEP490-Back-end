package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.TransactionRequest;
import com.sep.rookieservice.dto.TransactionResponse;
import com.sep.rookieservice.entity.PaymentMethod;
import com.sep.rookieservice.entity.Transaction;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.TransactionEnum;
import com.sep.rookieservice.mapper.TransactionMapper;
import com.sep.rookieservice.repository.TransactionRepository;
import com.sep.rookieservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;

    @Override
    @CacheEvict(value = {"allTransactions","Transaction"}, allEntries = true)
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
    @Cacheable(value = "allTransactions", key = "'search'")
    public Page<TransactionResponse> search(TransactionEnum status,
                                            IsActived isActived,
                                            String paymentMethodName,
                                            Pageable pageable) {
        String pmName = normalize(paymentMethodName);

        Transaction probe = new Transaction();
        if (status != null)    probe.setStatus(status.getStatus());
        if (isActived != null) probe.setIsActived(isActived);
        if (pmName != null) {
            PaymentMethod pm = new PaymentMethod();
            pm.setMethodName(pmName);
            probe.setPaymentMethod(pm);
        }

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnorePaths(
                        "transactionId","totalPrice","updatedAt","createdAt",
                        "paymentMethodId","orderId","orderCode","order"
                )
                // String matcher: contains + ignore-case CHO nested paymentMethod.methodName
                .withMatcher("paymentMethod.methodName", m -> m.contains().ignoreCase())
                .withIgnoreNullValues();

        Example<Transaction> example = Example.of(probe, matcher);

        return repository.findAll(example, pageable)
                .map(mapper::toResponse);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
