package com.kanflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public final class ComentarioDtos {

    private ComentarioDtos() {
    }

    public record ComentarioCreateRequest(
            @NotNull UUID autorId,
            @NotBlank @Size(max = 10000) String texto
    ) {
    }

    public record ComentarioUpdateRequest(
            @NotBlank @Size(max = 10000) String texto
    ) {
    }

    public record ComentarioResponse(
            UUID id,
            UUID cardId,
            UUID autorId,
            String autorNome,
            String texto,
            Instant criadoEm
    ) {
    }
}
