package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.UserAddressRequest;
import com.sep.rookieservice.dto.UserAddressResponse;

import java.util.List;

public interface UserAddressService {
    List<UserAddressResponse> getAll();
    UserAddressResponse getById(String id);
    List<UserAddressResponse> getByUserId(String userId);
    List<UserAddressResponse> create(List<UserAddressRequest> requests);
    UserAddressResponse update(String id, UserAddressRequest request);
    void softDelete(String id);
}
