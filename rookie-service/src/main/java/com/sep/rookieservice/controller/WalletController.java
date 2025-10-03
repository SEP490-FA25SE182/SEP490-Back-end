package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.WalletRequest;
import com.sep.rookieservice.dto.WalletResponse;
import com.sep.rookieservice.entity.Wallet;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.WalletService;
import com.sep.rookieservice.service.impl.WalletServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/wallets")
@RequiredArgsConstructor
@Validated
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public List<WalletResponse> getWallets() {
        return walletService.getAll();
    }

    @GetMapping("/{id}")
    public WalletResponse getWallet(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String id) {
        return walletService.getById(id);
    }

    @GetMapping("/user/{userId}")
    public WalletResponse getByUser(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String userId) {
        return walletService.getByUserId(userId);
    }

    // CREATE (danh sách)
    @PostMapping
    public List<WalletResponse> createWallets(@RequestBody @Valid List<WalletRequest> requests) {
        return walletService.create(requests);
    }

    // UPDATE
    @PutMapping("/{id}")
    public WalletResponse updateWallet(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid WalletRequest request) {
        return walletService.update(id, request);
    }

    // SOFT DELETE
    @DeleteMapping("/{id}")
    public void deleteWallet(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        walletService.softDelete(id);
    }

    // SEARCH
    @GetMapping("/search")
    public Page<WalletResponse> search(
            @RequestParam(required = false) IsActived isActived,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return walletService.search(isActived, pageable);
    }
}

