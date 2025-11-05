package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.RoleRequest;
import com.sep.rookieservice.dto.RoleResponse;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    List<RoleResponse> getAll();
    RoleResponse getById(String id);
    List<RoleResponse> create(List<RoleRequest> requests);
    RoleResponse update(String id, RoleRequest request);
    void softDelete(String id);
    Page<RoleResponse> search(
            String roleId,
            String roleName,
            IsActived isActived,
            Pageable pageable
    );
}
