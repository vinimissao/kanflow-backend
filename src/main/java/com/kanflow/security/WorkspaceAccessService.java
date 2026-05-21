package com.kanflow.security;

import com.kanflow.api.error.ForbiddenOperationException;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.entity.Usuario;
import com.kanflow.domain.entity.Workspace;
import com.kanflow.domain.entity.WorkspaceMember;
import com.kanflow.repository.UsuarioRepository;
import com.kanflow.repository.WorkspaceMemberRepository;
import com.kanflow.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceAccessService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilAuthorizer perfilAuthorizer;

    @Transactional(readOnly = true)
    public List<Workspace> listAccessible(UUID userId) {
        return workspaceMemberRepository.findAllAccessibleByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Workspace requireAccess(UUID userId, UUID workspaceId) {
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (canAccess(userId, w)) {
            return w;
        }
        throw new ResourceNotFoundException("Workspace não encontrado: " + workspaceId);
    }

    @Transactional(readOnly = true)
    public Workspace requireOwner(UUID userId, UUID workspaceId) {
        Workspace w = requireAccess(userId, workspaceId);
        if (!w.getOwner().getId().equals(userId)) {
            throw new ForbiddenOperationException("Apenas o dono do workspace pode executar esta ação.");
        }
        return w;
    }

    @Transactional(readOnly = true)
    public void requireRead(UUID userId, UUID workspaceId) {
        requireAccess(userId, workspaceId);
    }

    @Transactional(readOnly = true)
    public Workspace requireWrite(UUID userId, UUID workspaceId) {
        Workspace w = requireAccess(userId, workspaceId);
        perfilAuthorizer.assertCanWrite(userId);
        return w;
    }

    @Transactional
    public void ensureMember(UUID workspaceId, UUID usuarioId) {
        if (workspaceMemberRepository.existsByWorkspace_IdAndUsuario_Id(workspaceId, usuarioId)) {
            return;
        }
        Workspace w = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
        if (w.getOwner().getId().equals(usuarioId)) {
            return;
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + usuarioId));
        WorkspaceMember m = new WorkspaceMember();
        m.setWorkspace(w);
        m.setUsuario(usuario);
        workspaceMemberRepository.save(m);
    }

    private boolean canAccess(UUID userId, Workspace w) {
        if (w.getOwner().getId().equals(userId)) {
            return true;
        }
        return workspaceMemberRepository.existsByWorkspace_IdAndUsuario_Id(w.getId(), userId);
    }
}
