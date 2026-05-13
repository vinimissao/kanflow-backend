package com.kanflow.repository;

import com.kanflow.domain.entity.SprintSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SprintSnapshotRepository extends JpaRepository<SprintSnapshot, UUID> {
    Optional<SprintSnapshot> findBySprintHistoryId(UUID sprintHistoryId);

    @Query("select distinct s from SprintSnapshot s join fetch s.sprintHistory h where h.id in :ids")
    List<SprintSnapshot> findAllFetchedBySprintHistoryIdIn(@Param("ids") Collection<UUID> sprintHistoryIds);
}

