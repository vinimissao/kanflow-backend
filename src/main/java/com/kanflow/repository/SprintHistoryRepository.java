package com.kanflow.repository;

import com.kanflow.domain.entity.SprintHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SprintHistoryRepository extends JpaRepository<SprintHistory, UUID> {
    List<SprintHistory> findAllByWorkspaceIdOrderByNumeroDesc(UUID workspaceId);

    long countByWorkspace_Id(UUID workspaceId);

    @Query("select max(s.numero) from SprintHistory s where s.workspace.id = :workspaceId")
    Optional<Integer> findMaxNumeroByWorkspaceId(UUID workspaceId);
}

