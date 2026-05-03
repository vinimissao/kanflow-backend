package com.kanflow.api;

import com.kanflow.api.dto.CardDtos.CardCreateRequest;
import com.kanflow.api.dto.CardDtos.CardMoveRequest;
import com.kanflow.api.dto.CardDtos.CardPatchRequest;
import com.kanflow.api.dto.CardDtos.CardResponse;
import com.kanflow.api.dto.CardDtos.CardUpdateRequest;
import com.kanflow.service.CardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public List<CardResponse> listar(@RequestParam(required = false) UUID workspaceId) {
        if (workspaceId != null) {
            return cardService.listarPorWorkspace(workspaceId);
        }
        return cardService.listar();
    }

    @GetMapping("/{id}")
    public CardResponse buscar(@PathVariable UUID id) {
        return cardService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse criar(@Valid @RequestBody CardCreateRequest body) {
        return cardService.criar(body);
    }

    @PutMapping("/{id}")
    public CardResponse atualizar(@PathVariable UUID id, @Valid @RequestBody CardUpdateRequest body) {
        return cardService.atualizar(id, body);
    }

    @PatchMapping("/{id}")
    public CardResponse patch(@PathVariable UUID id, @Valid @RequestBody CardPatchRequest body) {
        return cardService.patch(id, body);
    }

    @PostMapping("/{id}/move")
    public CardResponse move(@PathVariable UUID id, @Valid @RequestBody CardMoveRequest body) {
        return cardService.move(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable UUID id) {
        cardService.excluir(id);
    }
}
