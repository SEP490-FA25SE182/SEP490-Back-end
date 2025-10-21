package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.RoleRequest;
import com.sep.rookieservice.dto.RoleResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.entity.Role;
import com.sep.rookieservice.mapper.RoleMapper;
import com.sep.rookieservice.repository.RoleRepository;
import com.sep.rookieservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allRoles", key = "'all'")
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Role", key = "#id")
    public RoleResponse getById(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found: " + id));
        return mapper.toResponse(role);
    }

    @Override
    @CacheEvict(value = {"allRoles", "Role"}, allEntries = true)
    public List<RoleResponse> create(List<RoleRequest> requests) {
        List<Role> entities = requests.stream().map(req -> {
            if (req.getRoleName() == null || req.getRoleName().isBlank()) {
                throw new IllegalArgumentException("roleName is required");
            }
            Role r = new Role();
            mapper.copyForCreate(req, r);
            if (r.getIsActived() == null) r.setIsActived(IsActived.ACTIVE);
            if (r.getCreatedAt() == null) r.setCreatedAt(Instant.now());
            return r;
        }).toList();

        return roleRepository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allRoles", "Role"}, allEntries = true)
    public RoleResponse update(String id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found: " + id));

        mapper.copyForUpdate(request, role);

        return mapper.toResponse(roleRepository.save(role));
    }

    @Override
    @CacheEvict(value = {"allRoles", "Role"}, allEntries = true)
    public void softDelete(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found: " + id));
        role.setIsActived(IsActived.INACTIVE);
        roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleResponse> search(
            String roleId,
            String roleName,
            IsActived isActived,
            Pageable pageable
    ) {
        String rid = normalize(roleId);
        String rn  = normalize(roleName);

        Role probe = new Role();
        if (rid != null) probe.setRoleId(rid);
        if (rn  != null) probe.setRoleName(rn);
        if (isActived != null) probe.setIsActived(isActived);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withMatcher("roleId", m -> m.exact())
                .withMatcher("roleName", m -> m.contains())
                .withIgnorePaths(
                        "createdAt",
                        "users"
                )
                .withIgnoreNullValues();

        return roleRepository.findAll(Example.of(probe, matcher), pageable)
                .map(mapper::toResponse);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}