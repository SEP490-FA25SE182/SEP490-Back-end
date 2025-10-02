package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.WalletRequest;
import com.sep.rookieservice.dto.WalletResponse;

import java.util.List;

public interface WalletService {
    List<WalletResponse> getAll();
    WalletResponse getById(String id);
    WalletResponse getByUserId(String userId);
    List<WalletResponse> create(List<WalletRequest> requests);
    WalletResponse update(String id, WalletRequest request);
    void softDelete(String id);
}

