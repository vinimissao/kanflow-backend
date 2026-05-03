package com.kanflow.repository;

import com.kanflow.domain.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoardColumnRepository extends JpaRepository<BoardColumn, UUID> {
    List<BoardColumn> findAllByWorkspaceIdOrderByOrdemAsc(UUID workspaceId);
    void deleteAllByWorkspaceId(UUID workspaceId);
}

