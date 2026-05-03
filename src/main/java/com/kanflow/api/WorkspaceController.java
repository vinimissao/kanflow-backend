package com.kanflow.api;

import com.kanflow.api.dto.WorkspaceDtos.BoardResponse;
import com.kanflow.api.dto.WorkspaceDtos.ColumnCreate;
import com.kanflow.api.dto.WorkspaceDtos.WorkspaceCreateRequest;
import com.kanflow.api.dto.WorkspaceDtos.WorkspaceResponse;
import com.kanflow.api.dto.WorkspaceDtos.WorkspaceUpdateRequest;
import com.kanflow.service.WorkspaceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces")
@Tag(name = "Workspaces", description = "Workspaces por usuário (owner) e board/colunas.")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping
    public List<WorkspaceResponse> list(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return workspaceService.list(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceResponse create(Authentication authentication, @Valid @RequestBody WorkspaceCreateRequest body) {
        UUID userId = (UUID) authentication.getPrincipal();
        return workspaceService.create(userId, body);
    }

    @GetMapping("/{id}")
    public WorkspaceResponse get(Authentication authentication, @PathVariable UUID id) {
        UUID userId = (UUID) authentication.getPrincipal();
        return workspaceService.get(userId, id);
    }

    @PutMapping("/{id}")
    public WorkspaceResponse update(Authentication authentication, @PathVariable UUID id, @Valid @RequestBody WorkspaceUpdateRequest body) {
        UUID userId = (UUID) authentication.getPrincipal();
        return workspaceService.update(userId, id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable UUID id) {
        UUID userId = (UUID) authentication.getPrincipal();
        workspaceService.delete(userId, id);
    }

    @GetMapping("/{id}/board")
    public BoardResponse getBoard(Authentication authentication, @PathVariable UUID id) {
        UUID userId = (UUID) authentication.getPrincipal();
        return workspaceService.getBoard(userId, id);
    }

    @PutMapping("/{id}/board")
    public BoardResponse putBoard(Authentication authentication, @PathVariable UUID id, @Valid @RequestBody List<ColumnCreate> body) {
        UUID userId = (UUID) authentication.getPrincipal();
        return workspaceService.putBoard(userId, id, body);
    }
}

