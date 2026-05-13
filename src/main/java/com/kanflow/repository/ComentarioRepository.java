package com.kanflow.repository;

import com.kanflow.domain.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ComentarioRepository extends JpaRepository<Comentario, UUID> {

    List<Comentario> findByCardIdOrderByCriadoEmAsc(UUID cardId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Comentario c WHERE c.card.id IN :ids")
    void deleteAllByCardIdIn(@Param("ids") Collection<UUID> ids);
}
