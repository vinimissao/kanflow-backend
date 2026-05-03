package com.kanflow.api.dto;

import com.kanflow.domain.enums.SprintStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public final class SprintDtos {

    private SprintDtos() {
    }

    public record SprintCreateRequest(
            @NotBlank @Size(max = 255) String nome,
            @NotNull LocalDate dataInicio,
            @NotNull LocalDate dataFim,
            @NotNull SprintStatus status
    ) {
    }

    public record SprintUpdateRequest(
            @NotBlank @Size(max = 255) String nome,
            @NotNull LocalDate dataInicio,
            @NotNull LocalDate dataFim,
            @NotNull SprintStatus status
    ) {
    }

    public record SprintResponse(
            UUID id,
            String nome,
            LocalDate dataInicio,
            LocalDate dataFim,
            SprintStatus status
    ) {
    }
}
