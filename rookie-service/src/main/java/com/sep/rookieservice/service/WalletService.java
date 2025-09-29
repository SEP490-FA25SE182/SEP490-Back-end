package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.WalletDto;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.model.Wallet;
import com.sep.rookieservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;

    @Cacheable(value = "allWallets", key = "'all'")
    public List<Wallet> getAllWallets() {
        System.out.println("⏳ Querying DB...");
        return walletRepository.findAll();
    }

    @CacheEvict(value = "allWallets", allEntries = true)
    public List<Wallet> createWallets(List<Wallet> wallets) {
        return walletRepository.saveAll(wallets);
    }

    @Cacheable(value = "Wallet", key = "'id'")
    public Optional<Wallet> findById(String id) {
        System.out.println("⏳ Querying Wallet by id...");
        return walletRepository.findById(id);
    }

    @CacheEvict(value = {"allWallets", "Wallet"}, allEntries = true)
    public Wallet updateWallet(String id, WalletDto dto) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + id));

        wallet.setCoin(dto.getCoin());
        wallet.setIsActived(dto.getIsActived());
        wallet.setUpdatedAt(Instant.now());

        return walletRepository.save(wallet);
    }

    @CacheEvict(value = {"allWallets", "Wallet"}, allEntries = true)
    public void deleteWallet(String id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + id));

        wallet.setIsActived(IsActived.INACTIVE);
        wallet.setUpdatedAt(Instant.now());

        walletRepository.save(wallet);
    }
}

