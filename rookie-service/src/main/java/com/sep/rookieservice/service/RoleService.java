package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.RoleRequest;
import com.sep.rookieservice.dto.RoleResponse;

import java.util.List;

public interface RoleService {
    List<RoleResponse> getAll();
    RoleResponse getById(String id);
    List<RoleResponse> create(List<RoleRequest> requests);
    RoleResponse update(String id, RoleRequest request);
    void softDelete(String id);
}
