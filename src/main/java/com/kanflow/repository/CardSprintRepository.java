package com.kanflow.repository;

import com.kanflow.domain.entity.CardSprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardSprintRepository extends JpaRepository<CardSprint, UUID> {

    boolean existsByCardIdAndSprintId(UUID cardId, UUID sprintId);

    Optional<CardSprint> findByCardIdAndSprintId(UUID cardId, UUID sprintId);

    List<CardSprint> findBySprintId(UUID sprintId);

    List<CardSprint> findByCardId(UUID cardId);
}
