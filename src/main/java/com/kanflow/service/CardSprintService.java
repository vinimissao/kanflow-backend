package com.kanflow.service;

import com.kanflow.api.dto.CardDtos.CardResponse;
import com.kanflow.api.error.ConflictException;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.entity.CardSprint;
import com.kanflow.repository.CardSprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardSprintService {

    private final CardSprintRepository cardSprintRepository;
    private final CardService cardService;
    private final SprintService sprintService;

    @Transactional
    public void vincular(UUID sprintId, UUID cardId) {
        if (cardSprintRepository.existsByCardIdAndSprintId(cardId, sprintId)) {
            throw new ConflictException("Card já vinculado a esta sprint");
        }
        CardSprint cs = new CardSprint();
        cs.setSprint(sprintService.getReference(sprintId));
        cs.setCard(cardService.getReference(cardId));
        cardSprintRepository.save(cs);
    }

    @Transactional
    public void desvincular(UUID sprintId, UUID cardId) {
        CardSprint cs = cardSprintRepository.findByCardIdAndSprintId(cardId, sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo card/sprint não encontrado"));
        cardSprintRepository.delete(cs);
    }

    @Transactional(readOnly = true)
    public List<CardResponse> listarCardsDaSprint(UUID sprintId) {
        sprintService.getReference(sprintId);
        return cardSprintRepository.findBySprintId(sprintId).stream()
                .map(cs -> cardService.buscar(cs.getCard().getId()))
                .toList();
    }
}
