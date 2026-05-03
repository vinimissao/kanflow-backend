package com.kanflow.api.dto;

import com.kanflow.domain.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public final class UsuarioDtos {

    private UsuarioDtos() {
    }

    public record UsuarioCreateRequest(
            @NotBlank @Size(max = 255) String nome,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 128) String senha,
            @NotNull PerfilUsuario perfil
    ) {
    }

    public record UsuarioUpdateRequest(
            @NotBlank @Size(max = 255) String nome,
            @NotBlank @Email @Size(max = 255) String email,
            @Size(min = 8, max = 128) String senha,
            @NotNull PerfilUsuario perfil
    ) {
    }

    public record UsuarioResponse(
            UUID id,
            String nome,
            String email,
            PerfilUsuario perfil,
            Instant criadoEm
    ) {
    }
}
