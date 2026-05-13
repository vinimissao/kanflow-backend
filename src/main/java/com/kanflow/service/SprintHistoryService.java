package com.kanflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanflow.api.dto.SprintHistoryDtos.SprintHistoryResponse;
import com.kanflow.api.dto.SprintHistoryDtos.SprintSnapshotResponse;
import com.kanflow.api.dto.SprintHistoryDtos.Snapshot;
import com.kanflow.api.dto.SprintHistoryDtos.CardSnap;
import com.kanflow.api.dto.SprintHistoryDtos.ColumnSnap;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.billing.BillingService;
import com.kanflow.domain.entity.BoardColumn;
import com.kanflow.domain.entity.Card;
import com.kanflow.domain.entity.SprintHistory;
import com.kanflow.domain.entity.SprintSnapshot;
import com.kanflow.domain.entity.Workspace;
import com.kanflow.domain.enums.CardStatus;
import com.kanflow.repository.BoardColumnRepository;
import com.kanflow.repository.CardRepository;
import com.kanflow.repository.CardSprintRepository;
import com.kanflow.repository.ChecklistItemRepository;
import com.kanflow.repository.ComentarioRepository;
import com.kanflow.repository.SprintHistoryRepository;
import com.kanflow.repository.SprintSnapshotRepository;
import com.kanflow.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SprintHistoryService {

    private final WorkspaceRepository workspaceRepository;
    private final CardRepository cardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final SprintHistoryRepository sprintHistoryRepository;
    private final SprintSnapshotRepository sprintSnapshotRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final CardSprintRepository cardSprintRepository;
    private final ComentarioRepository comentarioRepository;
    private final BillingService billingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public SprintSnapshotResponse completeSprint(UUID ownerId, UUID workspaceId) {
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (!w.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
        }
        billingService.assertSprintHistoryWrite(ownerId);

        int nextNumero = sprintHistoryRepository.findMaxNumeroByWorkspaceId(workspaceId)
                .map(n -> n + 1)
                .orElse(1);

        List<Card> cards = cardRepository.findAllByWorkspaceIdOrderByPosicaoAscCriadoEmAsc(workspaceId);
        int cardsTotal = cards.size();
        int cardsDone = (int) cards.stream().filter(c -> CardStatus.done.equals(c.getStatus())).count();
        List<UUID> cardIds = cards.stream().map(Card::getId).toList();
        int checklistTotal = 0;
        int checklistDone = 0;
        if (!cardIds.isEmpty()) {
            checklistTotal = (int) checklistItemRepository.countByCardIdsIn(cardIds);
            checklistDone = (int) checklistItemRepository.countConcluidosByCardIdsIn(cardIds);
        }

        Instant now = Instant.now();
        SprintHistory h = new SprintHistory();
        h.setWorkspace(w);
        h.setNumero(nextNumero);
        h.setStartedAt(now);
        h.setEndedAt(now);
        h.setCardsTotal(cardsTotal);
        h.setCardsDone(cardsDone);
        h.setChecklistTotal(checklistTotal);
        h.setChecklistDone(checklistDone);
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

        purgeWorkspaceCards(cards);

        return new SprintSnapshotResponse(
                savedHistory.getId(),
                savedHistory.getNumero(),
                savedHistory.getStartedAt(),
                savedHistory.getEndedAt(),
                snapshot,
                savedHistory.getCardsTotal(),
                savedHistory.getCardsDone(),
                savedHistory.getChecklistTotal(),
                savedHistory.getChecklistDone());
    }

    @Transactional
    public void blankBoard(UUID ownerId, UUID workspaceId) {
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (!w.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
        }
        List<Card> cards = cardRepository.findAllByWorkspaceIdOrderByPosicaoAscCriadoEmAsc(workspaceId);
        purgeWorkspaceCards(cards);
    }

    private void purgeWorkspaceCards(List<Card> cards) {
        if (cards.isEmpty()) {
            return;
        }
        List<UUID> ids = cards.stream().map(Card::getId).toList();
        cardSprintRepository.deleteAllByCardIdIn(ids);
        checklistItemRepository.deleteAllByCardIdIn(ids);
        comentarioRepository.deleteAllByCardIdIn(ids);
        cardRepository.deleteAllInBatch(cards);
    }

    @Transactional(readOnly = true)
    public List<SprintHistoryResponse> list(UUID ownerId, UUID workspaceId) {
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (!w.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
        }
        if (!billingService.sprintHistoryReadable(ownerId)) {
            return List.of();
        }
        List<SprintHistory> histories = sprintHistoryRepository.findAllByWorkspaceIdOrderByNumeroDesc(workspaceId);
        List<UUID> histIds = histories.stream().map(SprintHistory::getId).toList();
        Map<UUID, SprintSnapshot> snapByHistId = histIds.isEmpty()
                ? Map.of()
                : sprintSnapshotRepository.findAllFetchedBySprintHistoryIdIn(histIds).stream()
                .collect(Collectors.toMap(s -> s.getSprintHistory().getId(), Function.identity(), (a, b) -> a));

        return histories.stream()
                .map(h -> toHistoryResponse(h, workspaceId, snapByHistId.get(h.getId())))
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
        billingService.assertSprintHistoryWrite(ownerId);

        SprintSnapshot ss = sprintSnapshotRepository.findBySprintHistoryId(sprintHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Snapshot não encontrado: " + sprintHistoryId));

        Snapshot snapshot;
        try {
            snapshot = objectMapper.readValue(ss.getSnapshotJson(), Snapshot.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CardMetrics fromJson = parseCardMetricsLenient(ss.getSnapshotJson());
        int ct = nz(h.getCardsTotal());
        int cd = nz(h.getCardsDone());
        if (fromJson.total() > 0) {
            ct = fromJson.total();
            cd = fromJson.done();
        }

        return new SprintSnapshotResponse(
                h.getId(),
                h.getNumero(),
                h.getStartedAt(),
                h.getEndedAt(),
                snapshot,
                ct,
                cd,
                nz(h.getChecklistTotal()),
                nz(h.getChecklistDone()));
    }

    private SprintHistoryResponse toHistoryResponse(SprintHistory h, UUID workspaceId, SprintSnapshot snap) {
        int ct = nz(h.getCardsTotal());
        int cd = nz(h.getCardsDone());
        int clt = nz(h.getChecklistTotal());
        int cld = nz(h.getChecklistDone());

        if (snap != null) {
            CardMetrics m = parseCardMetricsLenient(snap.getSnapshotJson());
            if (m.total() > 0) {
                ct = m.total();
                cd = m.done();
            }
        }

        return new SprintHistoryResponse(
                h.getId(),
                workspaceId,
                h.getNumero(),
                h.getStartedAt(),
                h.getEndedAt(),
                ct,
                cd,
                clt,
                cld);
    }

    private record CardMetrics(int total, int done) {}

    private CardMetrics parseCardMetricsLenient(String json) {
        if (json == null || json.isBlank()) {
            return new CardMetrics(0, 0);
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode cardsNode = root.get("cards");
            if (cardsNode == null || !cardsNode.isArray() || cardsNode.isEmpty()) {
                return new CardMetrics(0, 0);
            }
            int total = cardsNode.size();
            int done = 0;
            for (JsonNode card : cardsNode) {
                JsonNode st = card.get("status");
                if (st != null && isDoneStatus(st.asText())) {
                    done++;
                }
            }
            return new CardMetrics(total, done);
        } catch (Exception e) {
            return new CardMetrics(0, 0);
        }
    }

    private static boolean isDoneStatus(String raw) {
        if (raw == null) {
            return false;
        }
        return "done".equalsIgnoreCase(raw.trim());
    }

    private static int nz(Integer v) {
        return v == null ? 0 : v;
    }

    private Snapshot buildSnapshot(UUID workspaceId) {
        List<BoardColumn> cols = boardColumnRepository.findAllByWorkspaceIdOrderByOrdemAsc(workspaceId);
        List<ColumnSnap> colSnap = cols.stream()
                .map(c -> new ColumnSnap(
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
                        c.getPontos() != null ? c.getPontos().toString() : null,
                        c.getTempoEstimado() != null ? c.getTempoEstimado().toString() : null,
                        c.getStatus(),
                        c.getPosicao()
                ))
                .toList();

        return new Snapshot(colSnap, cardSnap);
    }
}
