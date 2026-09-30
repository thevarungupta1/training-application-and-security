package com.thevarungupta.blog.rest.api.service.impl;

import com.thevarungupta.blog.rest.api.entity.Role;
import com.thevarungupta.blog.rest.api.entity.User;
import com.thevarungupta.blog.rest.api.payload.LoginRequest;
import com.thevarungupta.blog.rest.api.payload.RegisterRequest;
import com.thevarungupta.blog.rest.api.repository.RoleRepository;
import com.thevarungupta.blog.rest.api.repository.UserRepository;
import com.thevarungupta.blog.rest.api.security.JwtTokenProvider;
import com.thevarungupta.blog.rest.api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Override
    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return token;
    }

    @Override
    public String register(RegisterRequest request) {
        // check for username already exists in database
        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username is already taken!";
        }

        // check for email already exists in database
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email is already taken!";
        }

        // create user
        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // assign user role
        Set<Role> role = new HashSet<>();
        Role userRole = roleRepository.findByName(request.getRole()).get();
        role.add(userRole);
        user.setRoles(role);

        // save user
        userRepository.save(user);
        return "User registered successfully!";
    }
}
