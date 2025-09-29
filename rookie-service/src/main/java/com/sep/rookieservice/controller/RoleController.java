package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.RoleDto;
import com.sep.rookieservice.model.Role;
import com.sep.rookieservice.model.User;
import com.sep.rookieservice.service.RoleService;
import com.sep.rookieservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public List<Role> getRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public Role getRole(@PathVariable String id) {return roleService.findById(id).get();}

    @PostMapping
    public List<Role> createRoles(@RequestBody List<Role> roles) {
        return roleService.createRoles(roles);
    }

    @PutMapping("/{id}")
    public Role updateRole(@PathVariable String id, @RequestBody RoleDto roleDto) {
        return roleService.updateRole(id, roleDto);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
    }
}
