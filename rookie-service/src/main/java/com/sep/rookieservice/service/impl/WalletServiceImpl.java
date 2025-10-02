package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.WalletRequest;
import com.sep.rookieservice.dto.WalletResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.entity.Wallet;
import com.sep.rookieservice.mapper.WalletMapper;
import com.sep.rookieservice.repository.WalletRepository;
import com.sep.rookieservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allWallets", key = "'all'")
    public List<WalletResponse> getAll() {
        return walletRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Wallet", key = "#id")
    public WalletResponse getById(String id) {
        var w = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + id));
        return mapper.toResponse(w);
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getByUserId(String userId) {
        var w = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for userId: " + userId));
        return mapper.toResponse(w);
    }

    @Override
    @CacheEvict(value = {"allWallets", "Wallet"}, allEntries = true)
    public List<WalletResponse> create(List<WalletRequest> requests) {
        var entities = requests.stream().map(req -> {
            var w = new Wallet();
            mapper.copyForCreate(req, w);
            if (w.getIsActived() == null) w.setIsActived(IsActived.ACTIVE);
            if (w.getCreatedAt() == null) w.setCreatedAt(Instant.now());
            w.setUpdatedAt(Instant.now());
            if (w.getCoin() < 0) throw new IllegalArgumentException("coin must be >= 0");
            return w;
        }).toList();

        return walletRepository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allWallets", "Wallet"}, allEntries = true)
    public WalletResponse update(String id, WalletRequest request) {
        var wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + id));

        mapper.copyForUpdate(request, wallet);
        if (wallet.getCoin() < 0) throw new IllegalArgumentException("coin must be >= 0");
        wallet.setUpdatedAt(Instant.now());

        return mapper.toResponse(walletRepository.save(wallet));
    }

    @Override
    @CacheEvict(value = {"allWallets", "Wallet"}, allEntries = true)
    public void softDelete(String id) {
        var wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + id));
        wallet.setIsActived(IsActived.INACTIVE);
        wallet.setUpdatedAt(Instant.now());
        walletRepository.save(wallet);
    }
}

