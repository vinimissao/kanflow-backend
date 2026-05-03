package com.kanflow.repository;

import com.kanflow.domain.entity.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, UUID> {

    List<ChecklistItem> findByCardIdOrderByCriadoEmAsc(UUID cardId);
}
