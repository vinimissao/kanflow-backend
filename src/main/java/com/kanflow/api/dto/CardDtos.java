package com.kanflow.api.dto;

import com.kanflow.domain.enums.CardStatus;
import com.kanflow.domain.enums.Dificuldade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public final class CardDtos {

    private CardDtos() {
    }

    public record CardCreateRequest(
            @NotBlank @Size(max = 500) String titulo,
            String descricao,
            @NotNull Dificuldade dificuldade,
            Integer tempoEstimado,
            @NotNull CardStatus status,
            UUID responsavelId,
            @NotNull UUID workspaceId,
            Integer posicao,
            String assignee
    ) {
    }

    public record CardUpdateRequest(
            @NotBlank @Size(max = 500) String titulo,
            String descricao,
            @NotNull Dificuldade dificuldade,
            Integer tempoEstimado,
            @NotNull CardStatus status,
            UUID responsavelId,
            @NotNull UUID workspaceId,
            Integer posicao,
            String assignee
    ) {
    }

    // PATCH: campos opcionais (mantém compatibilidade com o front, que atualiza só status às vezes)
    public record CardPatchRequest(
            @Size(max = 500) String titulo,
            String descricao,
            Dificuldade dificuldade,
            Integer tempoEstimado,
            CardStatus status,
            UUID responsavelId,
            UUID workspaceId,
            Integer posicao,
            String assignee
    ) {}

    public record CardMoveRequest(
            @NotNull CardStatus status,
            Integer posicao
    ) {}

    public record CardResponse(
            UUID id,
            String titulo,
            String descricao,
            Dificuldade dificuldade,
            Integer tempoEstimado,
            CardStatus status,
            UUID responsavelId,
            String responsavelNome,
            UUID workspaceId,
            Integer posicao,
            String assignee,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
    }
}
