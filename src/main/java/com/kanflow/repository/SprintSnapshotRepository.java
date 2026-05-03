package com.kanflow.repository;

import com.kanflow.domain.entity.SprintSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SprintSnapshotRepository extends JpaRepository<SprintSnapshot, UUID> {
    Optional<SprintSnapshot> findBySprintHistoryId(UUID sprintHistoryId);
}

