package com.kanflow.api;

import com.kanflow.api.dto.ChecklistDtos.ChecklistItemCreateRequest;
import com.kanflow.api.dto.ChecklistDtos.ChecklistItemResponse;
import com.kanflow.api.dto.ChecklistDtos.ChecklistItemUpdateRequest;
import com.kanflow.service.ChecklistItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards/{cardId}/checklist-itens")
@Tag(name = "Checklist", description = "Itens de checklist vinculados a um card.")
@RequiredArgsConstructor
public class ChecklistItemController {

    private final ChecklistItemService checklistItemService;

    @GetMapping
    public List<ChecklistItemResponse> listar(@PathVariable UUID cardId) {
        return checklistItemService.listarPorCard(cardId);
    }

    @GetMapping("/{itemId}")
    public ChecklistItemResponse buscar(@PathVariable UUID cardId, @PathVariable UUID itemId) {
        return checklistItemService.buscar(cardId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistItemResponse criar(
            @PathVariable UUID cardId,
            @Valid @RequestBody ChecklistItemCreateRequest body) {
        return checklistItemService.criar(cardId, body);
    }

    @PutMapping("/{itemId}")
    public ChecklistItemResponse atualizar(
            @PathVariable UUID cardId,
            @PathVariable UUID itemId,
            @Valid @RequestBody ChecklistItemUpdateRequest body) {
        return checklistItemService.atualizar(cardId, itemId, body);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID cardId, @PathVariable UUID itemId) {
        checklistItemService.excluir(cardId, itemId);
    }
}
