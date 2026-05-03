package com.kanflow.repository;

import com.kanflow.domain.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComentarioRepository extends JpaRepository<Comentario, UUID> {

    List<Comentario> findByCardIdOrderByCriadoEmAsc(UUID cardId);
}
