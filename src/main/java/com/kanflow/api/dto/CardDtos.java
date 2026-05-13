package com.kanflow.api.dto;

import com.kanflow.domain.enums.CardStatus;
import com.kanflow.validation.StoryPoints;
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
            @NotNull @StoryPoints Integer pontos,
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
            @NotNull @StoryPoints Integer pontos,
            Integer tempoEstimado,
            @NotNull CardStatus status,
            UUID responsavelId,
            @NotNull UUID workspaceId,
            Integer posicao,
            String assignee
    ) {
    }

    public record CardPatchRequest(
            @Size(max = 500) String titulo,
            String descricao,
            @StoryPoints Integer pontos,
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
            Integer pontos,
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
