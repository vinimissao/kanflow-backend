package com.kanflow.service;

import com.kanflow.api.dto.WorkspaceDtos.BoardResponse;
import com.kanflow.api.dto.WorkspaceDtos.ColumnCreate;
import com.kanflow.api.dto.WorkspaceDtos.ColumnResponse;
import com.kanflow.api.dto.WorkspaceDtos.WorkspaceCreateRequest;
import com.kanflow.api.dto.WorkspaceDtos.WorkspaceResponse;
import com.kanflow.api.dto.WorkspaceDtos.WorkspaceUpdateRequest;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.entity.BoardColumn;
import com.kanflow.domain.entity.Usuario;
import com.kanflow.domain.entity.Workspace;
import com.kanflow.billing.BillingService;
import com.kanflow.repository.BoardColumnRepository;
import com.kanflow.repository.UsuarioRepository;
import com.kanflow.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final UsuarioRepository usuarioRepository;
    private final BillingService billingService;

    @Transactional(readOnly = true)
    public List<WorkspaceResponse> list(UUID ownerId) {
        return workspaceRepository.findAllByOwnerIdOrderByCriadoEmAsc(ownerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public WorkspaceResponse create(UUID ownerId, WorkspaceCreateRequest req) {
        billingService.assertCanCreateWorkspace(ownerId);
        Usuario owner = usuarioRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Workspace w = new Workspace();
        w.setOwner(owner);
        w.setNome(req.nome().trim());
        Workspace saved = workspaceRepository.save(w);

        List<ColumnCreate> cols = (req.columns() == null || req.columns().isEmpty())
                ? List.of(
                new ColumnCreate("To Do", 1),
                new ColumnCreate("Doing", 2),
                new ColumnCreate("Done", 3)
        )
                : req.columns().stream()
                .sorted(Comparator.comparing(ColumnCreate::ordem))
                .toList();

        for (ColumnCreate c : cols) {
            BoardColumn col = new BoardColumn();
            col.setWorkspace(saved);
            col.setNome(c.nome().trim());
            col.setOrdem(c.ordem());
            boardColumnRepository.save(col);
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public WorkspaceResponse get(UUID ownerId, UUID workspaceId) {
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (!w.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
        }
        return toResponse(w);
    }

    @Transactional
    public WorkspaceResponse update(UUID ownerId, UUID workspaceId, WorkspaceUpdateRequest req) {
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (!w.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
        }
        w.setNome(req.nome().trim());
        return toResponse(workspaceRepository.save(w));
    }

    @Transactional
    public void delete(UUID ownerId, UUID workspaceId) {
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (!w.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
        }
        workspaceRepository.delete(w);
    }

    @Transactional(readOnly = true)
    public BoardResponse getBoard(UUID ownerId, UUID workspaceId) {
        get(ownerId, workspaceId);
        List<ColumnResponse> cols = boardColumnRepository.findAllByWorkspaceIdOrderByOrdemAsc(workspaceId)
                .stream()
                .map(c -> new ColumnResponse(c.getId(), c.getNome(), c.getOrdem()))
                .toList();
        return new BoardResponse(workspaceId, cols);
    }

    @Transactional
    public BoardResponse putBoard(UUID ownerId, UUID workspaceId, List<ColumnCreate> columns) {
        get(ownerId, workspaceId);
        boardColumnRepository.deleteAllByWorkspaceId(workspaceId);
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));

        List<ColumnCreate> cols = (columns == null ? List.<ColumnCreate>of() : columns).stream()
                .sorted(Comparator.comparing(ColumnCreate::ordem))
                .toList();

        for (ColumnCreate c : cols) {
            BoardColumn col = new BoardColumn();
            col.setWorkspace(w);
            col.setNome(c.nome().trim());
            col.setOrdem(c.ordem());
            boardColumnRepository.save(col);
        }
        return getBoard(ownerId, workspaceId);
    }

    private WorkspaceResponse toResponse(Workspace w) {
        return new WorkspaceResponse(w.getId(), w.getNome(), w.getCriadoEm());
    }
}

