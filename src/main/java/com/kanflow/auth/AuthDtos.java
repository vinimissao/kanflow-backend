package com.kanflow.auth;

import com.kanflow.domain.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank @Size(max = 255) String nome,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 6, max = 200) String senha,
            @NotNull PerfilUsuario perfil
    ) {}

    public record LoginRequest(
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 6, max = 200) String senha
    ) {}

    public record AuthResponse(
            String accessToken,
            String tokenType,
            long expiresInSeconds
    ) {}

    public record ChangePasswordRequest(
            @NotBlank @Size(min = 6, max = 200) String senhaAtual,
            @NotBlank @Size(min = 6, max = 200) String novaSenha
    ) {}

    public record MeResponse(
            UUID id,
            String nome,
            String email,
            PerfilUsuario perfil,
            Instant criadoEm
    ) {}
}

