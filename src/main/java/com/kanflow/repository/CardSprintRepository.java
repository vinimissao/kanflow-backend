package com.kanflow.repository;

import com.kanflow.domain.entity.CardSprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardSprintRepository extends JpaRepository<CardSprint, UUID> {

    boolean existsByCardIdAndSprintId(UUID cardId, UUID sprintId);

    Optional<CardSprint> findByCardIdAndSprintId(UUID cardId, UUID sprintId);

    List<CardSprint> findBySprintId(UUID sprintId);

    List<CardSprint> findByCardId(UUID cardId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM CardSprint cs WHERE cs.card.id IN :ids")
    void deleteAllByCardIdIn(@Param("ids") Collection<UUID> ids);
}
