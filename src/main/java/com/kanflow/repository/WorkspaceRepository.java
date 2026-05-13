package com.kanflow.repository;

import com.kanflow.domain.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    List<Workspace> findAllByOwnerIdOrderByCriadoEmAsc(UUID ownerId);

    long countByOwner_Id(UUID ownerId);
}

