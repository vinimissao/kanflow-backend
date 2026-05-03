package com.kanflow.repository;

import com.kanflow.domain.entity.Card;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

    @EntityGraph(attributePaths = "responsavel")
    @Override
    List<Card> findAll();

    @EntityGraph(attributePaths = "responsavel")
    @Override
    Optional<Card> findById(UUID id);

    @EntityGraph(attributePaths = "responsavel")
    List<Card> findAllByWorkspaceIdOrderByPosicaoAscCriadoEmAsc(UUID workspaceId);

    @EntityGraph(attributePaths = "responsavel")
    @Query("""
            select c from Card c
            left join c.responsavel r
            where c.workspace.id = :workspaceId
              and (
                   lower(c.titulo) like lower(concat('%', :q, '%'))
                or lower(coalesce(c.descricao, '')) like lower(concat('%', :q, '%'))
                or lower(coalesce(c.assignee, '')) like lower(concat('%', :q, '%'))
                or lower(coalesce(r.nome, '')) like lower(concat('%', :q, '%'))
              )
            order by c.posicao asc nulls last, c.criadoEm asc
            """)
    List<Card> searchByWorkspace(UUID workspaceId, String q);
}
