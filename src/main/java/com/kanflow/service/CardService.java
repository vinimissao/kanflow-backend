package com.kanflow.service;

import com.kanflow.api.dto.CardDtos.CardCreateRequest;
import com.kanflow.api.dto.CardDtos.CardMoveRequest;
import com.kanflow.api.dto.CardDtos.CardPatchRequest;
import com.kanflow.api.dto.CardDtos.CardResponse;
import com.kanflow.api.dto.CardDtos.CardUpdateRequest;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.entity.Card;
import com.kanflow.domain.entity.Usuario;
import com.kanflow.domain.entity.Workspace;
import com.kanflow.repository.CardRepository;
import com.kanflow.repository.UsuarioRepository;
import com.kanflow.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UsuarioRepository usuarioRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional(readOnly = true)
    public List<CardResponse> listar() {
        return cardRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CardResponse> listarPorWorkspace(UUID workspaceId) {
        return cardRepository.findAllByWorkspaceIdOrderByPosicaoAscCriadoEmAsc(workspaceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CardResponse> buscarPorTexto(UUID workspaceId, String q) {
        if (q == null || q.isBlank()) {
            return listarPorWorkspace(workspaceId);
        }
        return cardRepository.searchByWorkspace(workspaceId, q.trim())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CardResponse buscar(UUID id) {
        return toResponse(cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card não encontrado: " + id)));
    }

    @Transactional
    public CardResponse criar(CardCreateRequest req) {
        Card c = new Card();
        c.setTitulo(req.titulo());
        c.setDescricao(req.descricao());
        c.setPontos(req.pontos());
        c.setTempoEstimado(req.tempoEstimado());
        c.setStatus(req.status());
        c.setResponsavel(resolveResponsavel(req.responsavelId()));
        c.setWorkspace(resolveWorkspace(req.workspaceId()));
        c.setPosicao(req.posicao());
        c.setAssignee(req.assignee());
        return toResponse(cardRepository.save(c));
    }

    @Transactional
    public CardResponse atualizar(UUID id, CardUpdateRequest req) {
        Card c = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card não encontrado: " + id));
        c.setTitulo(req.titulo());
        c.setDescricao(req.descricao());
        c.setPontos(req.pontos());
        c.setTempoEstimado(req.tempoEstimado());
        c.setStatus(req.status());
        c.setResponsavel(resolveResponsavel(req.responsavelId()));
        c.setWorkspace(resolveWorkspace(req.workspaceId()));
        c.setPosicao(req.posicao());
        c.setAssignee(req.assignee());
        return toResponse(cardRepository.save(c));
    }

    @Transactional
    public CardResponse patch(UUID id, CardPatchRequest req) {
        Card c = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card não encontrado: " + id));

        if (req.titulo() != null) c.setTitulo(req.titulo());
        if (req.descricao() != null) c.setDescricao(req.descricao());
        if (req.pontos() != null) c.setPontos(req.pontos());
        if (req.tempoEstimado() != null) c.setTempoEstimado(req.tempoEstimado());
        if (req.status() != null) c.setStatus(req.status());
        if (req.responsavelId() != null) c.setResponsavel(resolveResponsavel(req.responsavelId()));
        if (req.workspaceId() != null) c.setWorkspace(resolveWorkspace(req.workspaceId()));
        if (req.posicao() != null) c.setPosicao(req.posicao());
        if (req.assignee() != null) c.setAssignee(req.assignee());

        return toResponse(cardRepository.save(c));
    }

    @Transactional
    public CardResponse move(UUID id, CardMoveRequest req) {
        Card c = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card não encontrado: " + id));
        c.setStatus(req.status());
        if (req.posicao() != null) {
            c.setPosicao(req.posicao());
        }
        return toResponse(cardRepository.save(c));
    }

    @Transactional
    public void excluir(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Card não encontrado: " + id);
        }
        cardRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Card getReference(UUID id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card não encontrado: " + id));
    }

    private Usuario resolveResponsavel(UUID responsavelId) {
        if (responsavelId == null) {
            return null;
        }
        return usuarioRepository.findById(responsavelId)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado: " + responsavelId));
    }

    private Workspace resolveWorkspace(UUID workspaceId) {
        if (workspaceId == null) {
            throw new ResourceNotFoundException("workspaceId é obrigatório");
        }
        return workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace não encontrado: " + workspaceId));
    }

    private CardResponse toResponse(Card c) {
        Usuario r = c.getResponsavel();
        return new CardResponse(
                c.getId(),
                c.getTitulo(),
                c.getDescricao(),
                c.getPontos(),
                c.getTempoEstimado(),
                c.getStatus(),
                r != null ? r.getId() : null,
                r != null ? r.getNome() : null,
                c.getWorkspace() != null ? c.getWorkspace().getId() : null,
                c.getPosicao(),
                c.getAssignee(),
                c.getCriadoEm(),
                c.getAtualizadoEm()
        );
    }
}
