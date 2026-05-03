package com.kanflow.api;

import com.kanflow.api.dto.CardDtos.CardResponse;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.entity.Workspace;
import com.kanflow.repository.WorkspaceRepository;
import com.kanflow.service.CardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}")
@Tag(name = "Workspace Search", description = "Busca de cards por texto dentro do workspace.")
public class WorkspaceSearchController {

    private final WorkspaceRepository workspaceRepository;
    private final CardService cardService;

    public WorkspaceSearchController(WorkspaceRepository workspaceRepository, CardService cardService) {
        this.workspaceRepository = workspaceRepository;
        this.cardService = cardService;
    }

    @GetMapping("/search")
    public List<CardResponse> search(Authentication authentication, @PathVariable UUID workspaceId, @RequestParam String q) {
        UUID userId = (UUID) authentication.getPrincipal();
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (!w.getOwner().getId().equals(userId)) {
            throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
        }
        return cardService.buscarPorTexto(workspaceId, q);
    }
}

