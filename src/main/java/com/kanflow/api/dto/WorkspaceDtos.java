package com.kanflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class WorkspaceDtos {

    public record WorkspaceCreateRequest(
            @NotBlank @Size(max = 255) String nome,
            List<ColumnCreate> columns
    ) {}

    public record ColumnCreate(
            @NotBlank @Size(max = 120) String nome,
            @NotNull Integer ordem
    ) {}

    public record WorkspaceUpdateRequest(
            @NotBlank @Size(max = 255) String nome
    ) {}

    public record WorkspaceResponse(
            UUID id,
            String nome,
            Instant criadoEm
    ) {}

    public record ColumnResponse(
            UUID id,
            String nome,
            Integer ordem
    ) {}

    public record BoardResponse(
            UUID workspaceId,
            List<ColumnResponse> columns
    ) {}
}

