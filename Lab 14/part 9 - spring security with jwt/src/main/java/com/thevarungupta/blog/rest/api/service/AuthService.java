package com.thevarungupta.blog.rest.api.service;

import com.thevarungupta.blog.rest.api.payload.LoginRequest;
import com.thevarungupta.blog.rest.api.payload.RegisterRequest;

public interface AuthService {
    String login(LoginRequest request);
    String register(RegisterRequest request);
}
