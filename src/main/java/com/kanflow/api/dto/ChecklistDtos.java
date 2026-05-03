package com.kanflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public final class ChecklistDtos {

    private ChecklistDtos() {
    }

    public record ChecklistItemCreateRequest(
            @NotBlank @Size(max = 2000) String texto,
            boolean concluido
    ) {
    }

    public record ChecklistItemUpdateRequest(
            @NotBlank @Size(max = 2000) String texto,
            @NotNull Boolean concluido
    ) {
    }

    public record ChecklistItemResponse(
            UUID id,
            UUID cardId,
            String texto,
            boolean concluido,
            Instant criadoEm
    ) {
    }
}
