package com.thevarungupta.blog.rest.api.service.impl;

import com.thevarungupta.blog.rest.api.entity.Role;
import com.thevarungupta.blog.rest.api.repository.RoleRepository;
import com.thevarungupta.blog.rest.api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    public RoleRepository roleRepository;
    @Override
    public Role createRole(Role newRole) {
        return roleRepository.save(newRole);
    }

    @Override
    public Role getRoleByName(String roleName) {
        Role role = roleRepository
                .findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));
        return role;
    }

    @Override
    public Role getRoleById(Long roleId) {
        Role role = roleRepository
                .findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        return role;
    }
}
