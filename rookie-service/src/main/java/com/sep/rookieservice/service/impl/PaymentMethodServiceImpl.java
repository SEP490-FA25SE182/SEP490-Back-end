package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.PaymentMethodRequest;
import com.sep.rookieservice.dto.PaymentMethodResponse;
import com.sep.rookieservice.entity.PaymentMethod;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.mapper.PaymentMethodMapper;
import com.sep.rookieservice.repository.PaymentMethodRepository;
import com.sep.rookieservice.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository repository;
    private final PaymentMethodMapper mapper;

    @Override
    //@CacheEvict(value = {"allPaymentMethods","PaymentMethod"}, allEntries = true)
    public PaymentMethodResponse create(PaymentMethodRequest req) {
        if (req.getMethodName() != null &&
                repository.existsByMethodNameIgnoreCase(req.getMethodName().trim())) {
            throw new IllegalArgumentException("methodName đã tồn tại");
        }
        PaymentMethod e = new PaymentMethod();
        mapper.copyForCreate(req, e);
        return mapper.toResponse(repository.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "PaymentMethod", key = "#id")
    public PaymentMethodResponse getById(String id) {
        var e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod not found: " + id));
        return mapper.toResponse(e);
    }

    @Override
    @CacheEvict(value = {"allPaymentMethods","PaymentMethod"}, allEntries = true)
    public PaymentMethodResponse update(String id, PaymentMethodRequest req) {
        var e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod not found: " + id));

        if (req.getMethodName() != null) {
            String newName = req.getMethodName().trim();
            if (!newName.equalsIgnoreCase(e.getMethodName())
                    && repository.existsByMethodNameIgnoreCase(newName)) {
                throw new IllegalArgumentException("methodName đã tồn tại");
            }
        }

        mapper.copyForUpdate(req, e);
        return mapper.toResponse(repository.save(e));
    }

    @Override
    @CacheEvict(value = {"allPaymentMethods","PaymentMethod"}, allEntries = true)
    public void softDelete(String id) {
        var e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod not found: " + id));
        e.setIsActived(IsActived.INACTIVE);
        repository.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allPaymentMethods", key = "'search'")
    public Page<PaymentMethodResponse> search(String q, IsActived isActived, Pageable pageable) {
        String keyword = normalize(q);

        PaymentMethod probe = new PaymentMethod();
        if (isActived != null) probe.setIsActived(isActived);
        if (keyword != null) {
            probe.setMethodName(keyword);
            probe.setProvider(keyword);
        }

        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withIgnorePaths("paymentMethodId","decription","createdAt")
                .withIgnoreNullValues()
                .withMatcher("methodName", m -> m.contains().ignoreCase())
                .withMatcher("provider",   m -> m.contains().ignoreCase());

        // Switching sang matchingAll để không bắt buộc khớp 2 string:
        if (keyword == null) {
            matcher = ExampleMatcher.matchingAll()
                    .withIgnorePaths("paymentMethodId","methodName","provider","decription","createdAt")
                    .withIgnoreNullValues();
        }

        Example<PaymentMethod> example = Example.of(probe, matcher);
        return repository.findAll(example, pageable)
                .map(mapper::toResponse);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}

