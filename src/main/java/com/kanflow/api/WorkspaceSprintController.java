package com.kanflow.api;

import com.kanflow.api.dto.SprintHistoryDtos.SprintHistoryResponse;
import com.kanflow.api.dto.SprintHistoryDtos.SprintSnapshotResponse;
import com.kanflow.service.SprintHistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}")
@Tag(name = "Workspace Sprints", description = "Completar sprint (snapshot) e histórico por workspace.")
public class WorkspaceSprintController {

    private final SprintHistoryService sprintHistoryService;

    public WorkspaceSprintController(SprintHistoryService sprintHistoryService) {
        this.sprintHistoryService = sprintHistoryService;
    }

    @PostMapping("/sprints/complete")
    public SprintSnapshotResponse complete(Authentication authentication, @PathVariable UUID workspaceId) {
        UUID userId = (UUID) authentication.getPrincipal();
        return sprintHistoryService.completeSprint(userId, workspaceId);
    }

    @PostMapping("/board/blank")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blank(Authentication authentication, @PathVariable UUID workspaceId) {
        UUID userId = (UUID) authentication.getPrincipal();
        sprintHistoryService.blankBoard(userId, workspaceId);
    }

    @GetMapping("/sprints")
    public List<SprintHistoryResponse> list(Authentication authentication, @PathVariable UUID workspaceId) {
        UUID userId = (UUID) authentication.getPrincipal();
        return sprintHistoryService.list(userId, workspaceId);
    }

    @GetMapping("/sprints/{sprintHistoryId}")
    public SprintSnapshotResponse get(Authentication authentication, @PathVariable UUID workspaceId, @PathVariable UUID sprintHistoryId) {
        UUID userId = (UUID) authentication.getPrincipal();
        return sprintHistoryService.getSnapshot(userId, workspaceId, sprintHistoryId);
    }
}

