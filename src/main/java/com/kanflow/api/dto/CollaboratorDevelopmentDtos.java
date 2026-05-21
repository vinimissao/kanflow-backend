package com.kanflow.api.dto;

import java.util.UUID;

public final class CollaboratorDevelopmentDtos {

    private CollaboratorDevelopmentDtos() {
    }

    public record CollaboratorDevelopmentResponse(
            UUID responsavelId,
            String nome,
            int cardsConcluidos,
            int cardsTotais,
            int pontos,
            int pontosConcluidos,
            int checklistItensTotal,
            int checklistItensConcluidos,
            int checklistPercent
    ) {
    }
}
