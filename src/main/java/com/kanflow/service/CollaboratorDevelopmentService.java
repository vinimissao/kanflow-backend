package com.kanflow.service;

import com.kanflow.api.dto.CollaboratorDevelopmentDtos.CollaboratorDevelopmentResponse;
import com.kanflow.domain.entity.Card;
import com.kanflow.domain.entity.Usuario;
import com.kanflow.domain.enums.CardStatus;
import com.kanflow.domain.enums.PerfilUsuario;
import com.kanflow.repository.CardRepository;
import com.kanflow.repository.ChecklistItemRepository;
import com.kanflow.security.PerfilAuthorizer;
import com.kanflow.security.WorkspaceAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollaboratorDevelopmentService {

    private final WorkspaceAccessService workspaceAccessService;
    private final PerfilAuthorizer perfilAuthorizer;
    private final CardRepository cardRepository;
    private final ChecklistItemRepository checklistItemRepository;

    @Transactional(readOnly = true)
    public List<CollaboratorDevelopmentResponse> ranking(UUID userId, UUID workspaceId) {
        workspaceAccessService.requireRead(userId, workspaceId);
        List<Card> cards = cardRepository.findAllByWorkspaceIdOrderByPosicaoAscCriadoEmAsc(workspaceId);
        Map<UUID, MutableStats> byUser = new HashMap<>();

        for (Card card : cards) {
            Usuario responsavel = card.getResponsavel();
            if (responsavel == null) {
                continue;
            }
            UUID responsavelId = responsavel.getId();
            MutableStats stats = byUser.computeIfAbsent(responsavelId, id -> new MutableStats(responsavel));
            stats.cardsTotais++;
            int pontos = card.getPontos() != null ? card.getPontos() : 0;
            stats.pontosTotais += pontos;
            if (CardStatus.done.equals(card.getStatus())) {
                stats.cardsConcluidos++;
                stats.pontosConcluidos += pontos;
            }
            stats.cardIds.add(card.getId());
        }

        for (MutableStats stats : byUser.values()) {
            if (stats.cardIds.isEmpty()) {
                continue;
            }
            stats.checklistItensTotal = (int) checklistItemRepository.countByCardIdsIn(stats.cardIds);
            stats.checklistItensConcluidos = (int) checklistItemRepository.countConcluidosByCardIdsIn(stats.cardIds);
        }

        List<CollaboratorDevelopmentResponse> ranking = byUser.values().stream()
                .map(MutableStats::toResponse)
                .sorted(Comparator
                        .comparingInt(CollaboratorDevelopmentResponse::pontos)
                        .reversed()
                        .thenComparingInt(CollaboratorDevelopmentResponse::cardsConcluidos)
                        .reversed())
                .toList();

        if (perfilAuthorizer.requirePerfil(userId) == PerfilUsuario.admin) {
            return ranking;
        }
        return ranking.stream()
                .filter(r -> r.responsavelId().equals(userId))
                .toList();
    }

    private static final class MutableStats {
        private final Usuario usuario;
        private int cardsTotais;
        private int cardsConcluidos;
        private int pontosTotais;
        private int pontosConcluidos;
        private int checklistItensTotal;
        private int checklistItensConcluidos;
        private final List<UUID> cardIds = new ArrayList<>();

        MutableStats(Usuario usuario) {
            this.usuario = usuario;
        }

        CollaboratorDevelopmentResponse toResponse() {
            int checklistPercent = checklistItensTotal == 0
                    ? 0
                    : (int) Math.round(100.0 * checklistItensConcluidos / checklistItensTotal);
            return new CollaboratorDevelopmentResponse(
                    usuario.getId(),
                    usuario.getNome(),
                    cardsConcluidos,
                    cardsTotais,
                    pontosTotais,
                    pontosConcluidos,
                    checklistItensTotal,
                    checklistItensConcluidos,
                    checklistPercent);
        }
    }
}
