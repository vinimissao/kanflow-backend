package com.kanflow.service;

import com.kanflow.api.dto.ChecklistDtos.ChecklistItemCreateRequest;
import com.kanflow.api.dto.ChecklistDtos.ChecklistItemResponse;
import com.kanflow.api.dto.ChecklistDtos.ChecklistItemUpdateRequest;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.entity.Card;
import com.kanflow.domain.entity.ChecklistItem;
import com.kanflow.repository.ChecklistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChecklistItemService {

    private final ChecklistItemRepository checklistItemRepository;
    private final CardService cardService;

    @Transactional(readOnly = true)
    public List<ChecklistItemResponse> listarPorCard(UUID cardId) {
        cardService.getReference(cardId);
        return checklistItemRepository.findByCardIdOrderByCriadoEmAsc(cardId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChecklistItemResponse buscar(UUID cardId, UUID itemId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de checklist não encontrado: " + itemId));
        ensureCard(item, cardId);
        return toResponse(item);
    }

    @Transactional
    public ChecklistItemResponse criar(UUID cardId, ChecklistItemCreateRequest req) {
        Card card = cardService.getReference(cardId);
        ChecklistItem item = new ChecklistItem();
        item.setCard(card);
        item.setTexto(req.texto());
        item.setConcluido(req.concluido());
        return toResponse(checklistItemRepository.save(item));
    }

    @Transactional
    public ChecklistItemResponse atualizar(UUID cardId, UUID itemId, ChecklistItemUpdateRequest req) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de checklist não encontrado: " + itemId));
        ensureCard(item, cardId);
        item.setTexto(req.texto());
        item.setConcluido(req.concluido());
        return toResponse(checklistItemRepository.save(item));
    }

    @Transactional
    public void excluir(UUID cardId, UUID itemId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de checklist não encontrado: " + itemId));
        ensureCard(item, cardId);
        checklistItemRepository.delete(item);
    }

    private void ensureCard(ChecklistItem item, UUID cardId) {
        if (!item.getCard().getId().equals(cardId)) {
            throw new ResourceNotFoundException("Item de checklist não encontrado neste card: " + item.getId());
        }
    }

    private ChecklistItemResponse toResponse(ChecklistItem item) {
        return new ChecklistItemResponse(
                item.getId(),
                item.getCard().getId(),
                item.getTexto(),
                item.isConcluido(),
                item.getCriadoEm()
        );
    }
}
