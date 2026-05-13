package com.kanflow.repository;

import com.kanflow.domain.entity.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, UUID> {

    List<ChecklistItem> findByCardIdOrderByCriadoEmAsc(UUID cardId);

    @Query("SELECT COUNT(i) FROM ChecklistItem i WHERE i.card.id IN :ids")
    long countByCardIdsIn(@Param("ids") Collection<UUID> ids);

    @Query("SELECT COUNT(i) FROM ChecklistItem i WHERE i.card.id IN :ids AND i.concluido = true")
    long countConcluidosByCardIdsIn(@Param("ids") Collection<UUID> ids);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM ChecklistItem i WHERE i.card.id IN :ids")
    void deleteAllByCardIdIn(@Param("ids") Collection<UUID> ids);
}
