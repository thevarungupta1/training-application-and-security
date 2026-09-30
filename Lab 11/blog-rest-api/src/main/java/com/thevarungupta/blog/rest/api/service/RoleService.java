package com.thevarungupta.blog.rest.api.service;

import com.thevarungupta.blog.rest.api.entity.Role;

public interface RoleService {
    Role createRole(Role newRole);
    Role getRoleByName(String roleName);
    Role getRoleById(Long roleId);
}
