package com.example.personal_management_app.user.core.controller;

import com.example.personal_management_app.core.responses.RestApiResponse;
import com.example.personal_management_app.user.core.dtos.AuthResponse;
import com.example.personal_management_app.user.core.dtos.LoginRequest;
import com.example.personal_management_app.user.core.dtos.RegisterRequest;
import com.example.personal_management_app.user.core.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<RestApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return RestApiResponse.success(response, "Login successful");
    }

    @PostMapping("/register")
    public ResponseEntity<RestApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return RestApiResponse.created(response, "Registration successful");
    }
}
