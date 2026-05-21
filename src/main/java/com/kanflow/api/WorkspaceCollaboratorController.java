package com.kanflow.api;

import com.kanflow.api.dto.CollaboratorDevelopmentDtos.CollaboratorDevelopmentResponse;
import com.kanflow.service.CollaboratorDevelopmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/collaborators")
@Tag(name = "Colaboradores", description = "Métricas de desenvolvimento por responsável no workspace.")
public class WorkspaceCollaboratorController {

    private final CollaboratorDevelopmentService collaboratorDevelopmentService;

    public WorkspaceCollaboratorController(CollaboratorDevelopmentService collaboratorDevelopmentService) {
        this.collaboratorDevelopmentService = collaboratorDevelopmentService;
    }

    @GetMapping("/development")
    public List<CollaboratorDevelopmentResponse> development(
            Authentication authentication, @PathVariable UUID workspaceId) {
        UUID userId = (UUID) authentication.getPrincipal();
        return collaboratorDevelopmentService.ranking(userId, workspaceId);
    }
}
