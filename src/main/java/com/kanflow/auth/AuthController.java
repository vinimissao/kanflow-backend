package com.kanflow.auth;

import com.kanflow.auth.AuthDtos.AuthResponse;
import com.kanflow.auth.AuthDtos.ChangePasswordRequest;
import com.kanflow.auth.AuthDtos.LoginRequest;
import com.kanflow.auth.AuthDtos.MeResponse;
import com.kanflow.auth.AuthDtos.RegisterRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Cadastro, login e endpoint /me via JWT Bearer.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest body) {
        return authService.register(body);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest body) {
        return authService.login(body);
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return authService.me(userId);
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest body) {
        UUID userId = (UUID) authentication.getPrincipal();
        authService.changePassword(userId, body);
    }
}

