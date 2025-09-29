package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.WalletDto;
import com.sep.rookieservice.model.Wallet;
import com.sep.rookieservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping
    public List<Wallet> getWallets() {
        return walletService.getAllWallets();
    }

    @GetMapping("/{id}")
    public Wallet getWallet(@PathVariable String id) {
        return walletService.findById(id).get();
    }

    @PostMapping
    public List<Wallet> createWallets(@RequestBody List<Wallet> wallets) {
        return walletService.createWallets(wallets);
    }

    @PutMapping("/{id}")
    public Wallet updateWallet(@PathVariable String id, @RequestBody WalletDto dto) {
        return walletService.updateWallet(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteWallet(@PathVariable String id) {
        walletService.deleteWallet(id);
    }
}

