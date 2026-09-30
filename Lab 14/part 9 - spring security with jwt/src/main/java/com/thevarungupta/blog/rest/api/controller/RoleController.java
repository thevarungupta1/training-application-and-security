package com.thevarungupta.blog.rest.api.controller;

import com.thevarungupta.blog.rest.api.entity.Role;
import com.thevarungupta.blog.rest.api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/roles")
@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role newRole) {
        var createdRole = roleService.createRole(newRole);
        return ResponseEntity.ok(createdRole);
    }

    @GetMapping("/{roleName}")
    public ResponseEntity<Role> getRoleByName(@PathVariable String roleName) {
        var role = roleService.getRoleByName(roleName);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/id/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long roleId) {
        var role = roleService.getRoleById(roleId);
        return ResponseEntity.ok(role);
    }
}
