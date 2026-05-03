package com.kanflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanflow.api.dto.SprintHistoryDtos.SprintHistoryResponse;
import com.kanflow.api.dto.SprintHistoryDtos.SprintSnapshotResponse;
import com.kanflow.api.dto.SprintHistoryDtos.Snapshot;
import com.kanflow.api.dto.SprintHistoryDtos.CardSnap;
import com.kanflow.api.dto.SprintHistoryDtos.ColumnSnap;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.entity.BoardColumn;
import com.kanflow.domain.entity.Card;
import com.kanflow.domain.entity.SprintHistory;
import com.kanflow.domain.entity.SprintSnapshot;
import com.kanflow.domain.entity.Workspace;
import com.kanflow.repository.BoardColumnRepository;
import com.kanflow.repository.CardRepository;
import com.kanflow.repository.SprintHistoryRepository;
import com.kanflow.repository.SprintSnapshotRepository;
import com.kanflow.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SprintHistoryService {

    private final WorkspaceRepository workspaceRepository;
    private final CardRepository cardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final SprintHistoryRepository sprintHistoryRepository;
    private final SprintSnapshotRepository sprintSnapshotRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public SprintSnapshotResponse completeSprint(UUID ownerId, UUID workspaceId) {
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (!w.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
        }

        int nextNumero = sprintHistoryRepository.findMaxNumeroByWorkspaceId(workspaceId)
                .map(n -> n + 1)
                .orElse(1);

        Instant now = Instant.now();
        SprintHistory h = new SprintHistory();
        h.setWorkspace(w);
        h.setNumero(nextNumero);
        h.setStartedAt(now); // se quiser, dá pra guardar "startedAt" do sprint atual; por ora, marca como now
        h.setEndedAt(now);
        SprintHistory savedHistory = sprintHistoryRepository.save(h);

        Snapshot snapshot = buildSnapshot(workspaceId);
        String json;
        try {
            json = objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SprintSnapshot ss = new SprintSnapshot();
        ss.setSprintHistory(savedHistory);
        ss.setSnapshotJson(json);
        sprintSnapshotRepository.save(ss);

        // abre "board em branco" removendo cards atuais do workspace
        List<Card> cards = cardRepository.findAllByWorkspaceIdOrderByPosicaoAscCriadoEmAsc(workspaceId);
        cardRepository.deleteAll(cards);

        return new SprintSnapshotResponse(savedHistory.getId(), savedHistory.getNumero(), savedHistory.getStartedAt(), savedHistory.getEndedAt(), snapshot);
    }

    @Transactional
    public void blankBoard(UUID ownerId, UUID workspaceId) {
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (!w.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
        }
        List<Card> cards = cardRepository.findAllByWorkspaceIdOrderByPosicaoAscCriadoEmAsc(workspaceId);
        cardRepository.deleteAll(cards);
    }

    @Transactional(readOnly = true)
    public List<SprintHistoryResponse> list(UUID ownerId, UUID workspaceId) {
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (!w.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
        }
        return sprintHistoryRepository.findAllByWorkspaceIdOrderByNumeroDesc(workspaceId)
                .stream()
                .map(h -> new SprintHistoryResponse(h.getId(), workspaceId, h.getNumero(), h.getStartedAt(), h.getEndedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public SprintSnapshotResponse getSnapshot(UUID ownerId, UUID workspaceId, UUID sprintHistoryId) {
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (!w.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
        }

        SprintHistory h = sprintHistoryRepository.findById(sprintHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint não encontrada: " + sprintHistoryId));
        if (!h.getWorkspace().getId().equals(workspaceId)) {
            throw new ResourceNotFoundException("Sprint não encontrada: " + sprintHistoryId);
        }

        SprintSnapshot ss = sprintSnapshotRepository.findBySprintHistoryId(sprintHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Snapshot não encontrado: " + sprintHistoryId));

        Snapshot snapshot;
        try {
            snapshot = objectMapper.readValue(ss.getSnapshotJson(), Snapshot.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new SprintSnapshotResponse(h.getId(), h.getNumero(), h.getStartedAt(), h.getEndedAt(), snapshot);
    }

    private Snapshot buildSnapshot(UUID workspaceId) {
        List<BoardColumn> cols = boardColumnRepository.findAllByWorkspaceIdOrderByOrdemAsc(workspaceId);
        List<ColumnSnap> colSnap = cols.stream()
                .map(c -> new ColumnSnap(
                        // status no front é string; aqui vamos mapear title->nome e usar status=nome lower/slug? Melhor: usar status igual ao enum do card.
                        // Como o front usa status fixo, a coluna real do snapshot vem do próprio card.status.
                        c.getNome(),
                        c.getNome(),
                        c.getOrdem()
                ))
                .toList();

        List<Card> cards = cardRepository.findAllByWorkspaceIdOrderByPosicaoAscCriadoEmAsc(workspaceId);
        List<CardSnap> cardSnap = cards.stream()
                .sorted(Comparator
                        .comparing((Card c) -> c.getStatus() != null ? c.getStatus().name() : "")
                        .thenComparing(c -> c.getPosicao() != null ? c.getPosicao() : Integer.MAX_VALUE)
                        .thenComparing(Card::getCriadoEm))
                .map(c -> new CardSnap(
                        c.getId(),
                        c.getTitulo(),
                        c.getDescricao(),
                        c.getAssignee(),
                        c.getDificuldade() != null ? c.getDificuldade().name() : null,
                        c.getTempoEstimado() != null ? c.getTempoEstimado().toString() : null,
                        c.getStatus(),
                        c.getPosicao()
                ))
                .toList();

        return new Snapshot(colSnap, cardSnap);
    }
}

