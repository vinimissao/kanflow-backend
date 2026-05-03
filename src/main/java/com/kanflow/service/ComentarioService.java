package com.kanflow.service;

import com.kanflow.api.dto.ComentarioDtos.ComentarioCreateRequest;
import com.kanflow.api.dto.ComentarioDtos.ComentarioResponse;
import com.kanflow.api.dto.ComentarioDtos.ComentarioUpdateRequest;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.entity.Card;
import com.kanflow.domain.entity.Comentario;
import com.kanflow.domain.entity.Usuario;
import com.kanflow.repository.ComentarioRepository;
import com.kanflow.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CardService cardService;

    @Transactional(readOnly = true)
    public List<ComentarioResponse> listarPorCard(UUID cardId) {
        cardService.getReference(cardId);
        return comentarioRepository.findByCardIdOrderByCriadoEmAsc(cardId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ComentarioResponse buscar(UUID cardId, UUID comentarioId) {
        Comentario c = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado: " + comentarioId));
        ensureCard(c, cardId);
        return toResponse(c);
    }

    @Transactional
    public ComentarioResponse criar(UUID cardId, ComentarioCreateRequest req) {
        Card card = cardService.getReference(cardId);
        Usuario autor = usuarioRepository.findById(req.autorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado: " + req.autorId()));
        Comentario c = new Comentario();
        c.setCard(card);
        c.setAutor(autor);
        c.setTexto(req.texto());
        return toResponse(comentarioRepository.save(c));
    }

    @Transactional
    public ComentarioResponse atualizar(UUID cardId, UUID comentarioId, ComentarioUpdateRequest req) {
        Comentario c = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado: " + comentarioId));
        ensureCard(c, cardId);
        c.setTexto(req.texto());
        return toResponse(comentarioRepository.save(c));
    }

    @Transactional
    public void excluir(UUID cardId, UUID comentarioId) {
        Comentario c = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado: " + comentarioId));
        ensureCard(c, cardId);
        comentarioRepository.delete(c);
    }

    private void ensureCard(Comentario c, UUID cardId) {
        if (!c.getCard().getId().equals(cardId)) {
            throw new ResourceNotFoundException("Comentário não encontrado neste card: " + c.getId());
        }
    }

    private ComentarioResponse toResponse(Comentario c) {
        return new ComentarioResponse(
                c.getId(),
                c.getCard().getId(),
                c.getAutor().getId(),
                c.getAutor().getNome(),
                c.getTexto(),
                c.getCriadoEm()
        );
    }
}
