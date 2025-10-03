package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.UserAddressRequest;
import com.sep.rookieservice.dto.UserAddressResponse;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserAddressService {
    List<UserAddressResponse> getAll();
    UserAddressResponse getById(String id);
    List<UserAddressResponse> getByUserId(String userId);
    List<UserAddressResponse> create(List<UserAddressRequest> requests);
    UserAddressResponse update(String id, UserAddressRequest request);
    void softDelete(String id);
    Page<UserAddressResponse> search(IsActived isActived, Pageable pageable);
}
