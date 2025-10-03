package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.WalletRequest;
import com.sep.rookieservice.dto.WalletResponse;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WalletService {
    List<WalletResponse> getAll();
    WalletResponse getById(String id);
    WalletResponse getByUserId(String userId);
    List<WalletResponse> create(List<WalletRequest> requests);
    WalletResponse update(String id, WalletRequest request);
    void softDelete(String id);
    Page<WalletResponse> search(IsActived isActived, Pageable pageable);
}

