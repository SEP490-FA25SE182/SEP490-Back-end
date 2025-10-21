package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.RoleRequest;
import com.sep.rookieservice.dto.RoleResponse;
import com.sep.rookieservice.entity.Role;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.RoleService;
import com.sep.rookieservice.service.impl.RoleServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public List<RoleResponse> getRoles() {
        return roleService.getAll();
    }

    @GetMapping("/{id}")
    public RoleResponse getRole(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String id) {
        return roleService.getById(id);
    }

    @PostMapping
    public List<RoleResponse> createRoles(@RequestBody @Valid List<RoleRequest> requests) {
        return roleService.create(requests);
    }

    @PutMapping("/{id}")
    public RoleResponse updateRole(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid RoleRequest request) {
        return roleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        roleService.softDelete(id);
    }

    @GetMapping("/search")
    public Page<RoleResponse> searchRoles(
            @RequestParam(required = false)
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String roleId,
            @RequestParam(required = false) @Size(max = 50)
            String roleName,
            @RequestParam(required = false)
            IsActived isActived,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        return roleService.search(roleId, roleName, isActived, pageable);
    }
}
