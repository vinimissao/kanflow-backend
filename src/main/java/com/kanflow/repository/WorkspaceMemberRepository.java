package com.kanflow.repository;

import com.kanflow.domain.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {

    boolean existsByWorkspace_IdAndUsuario_Id(UUID workspaceId, UUID usuarioId);

    Optional<WorkspaceMember> findByWorkspace_IdAndUsuario_Id(UUID workspaceId, UUID usuarioId);

    @Query("""
            select distinct w from Workspace w
            left join WorkspaceMember m on m.workspace.id = w.id
            where w.owner.id = :userId or m.usuario.id = :userId
            order by w.criadoEm asc
            """)
    List<com.kanflow.domain.entity.Workspace> findAllAccessibleByUserId(UUID userId);
}
