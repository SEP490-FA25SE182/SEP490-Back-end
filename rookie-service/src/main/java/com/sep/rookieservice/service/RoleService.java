package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.RoleDto;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.model.Role;
import com.sep.rookieservice.model.User;
import com.sep.rookieservice.repository.RoleRepository;
import com.sep.rookieservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @Cacheable(value = "allRoles", key = "'all'")
    public List<Role> getAllRoles() {
        System.out.println("⏳ Querying DB...");
        return roleRepository.findAll();
    }

    @CacheEvict(value = "allRoles", allEntries = true)
    public List<Role> createRoles(List<Role> roles) {
        return roleRepository.saveAll(roles);
    }

    @Cacheable(value = "Role", key = "'id'")
    public Optional<Role> findById(String id) {
        System.out.println("⏳ Querying Role by id...");
        return  roleRepository.findById(id);
    }

    @CacheEvict(value = {"allRoles", "Role"}, allEntries = true)
    public Role updateRole(String id, RoleDto roleDto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        role.setRoleName(roleDto.getRoleName());
        role.setIsActived(roleDto.getIsActived());

        return roleRepository.save(role);
    }

    @CacheEvict(value = {"allRoles", "Role"}, allEntries = true)
    public void deleteRole(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        role.setIsActived(IsActived.INACTIVE);
        roleRepository.save(role);
    }
}
