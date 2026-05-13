package com.kanflow.api.dto;

import com.kanflow.domain.enums.CardStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SprintHistoryDtos {

    public record SprintHistoryResponse(
            UUID id,
            UUID workspaceId,
            Integer numero,
            Instant startedAt,
            Instant endedAt,
            int cardsTotal,
            int cardsDone,
            int checklistTotal,
            int checklistDone
    ) {}

    public record SprintSnapshotResponse(
            UUID sprintHistoryId,
            Integer numero,
            Instant startedAt,
            Instant endedAt,
            Snapshot snapshot,
            int cardsTotal,
            int cardsDone,
            int checklistTotal,
            int checklistDone
    ) {}

    public record Snapshot(
            List<ColumnSnap> columns,
            List<CardSnap> cards
    ) {}

    public record ColumnSnap(
            String status,
            String title,
            Integer order
    ) {}

    public record CardSnap(
            UUID id,
            String title,
            String description,
            String assignee,
            String difficulty,
            String developmentTime,
            CardStatus status,
            Integer position
    ) {}
}

