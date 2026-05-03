package com.kanflow.api;

import com.kanflow.api.dto.CardDtos.CardResponse;
import com.kanflow.api.dto.SprintDtos.SprintCreateRequest;
import com.kanflow.api.dto.SprintDtos.SprintResponse;
import com.kanflow.api.dto.SprintDtos.SprintUpdateRequest;
import com.kanflow.service.CardSprintService;
import com.kanflow.service.SprintService;
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
@RequestMapping("/api/sprints")
@Tag(name = "Sprints", description = "CRUD de sprints e vínculo N:N card–sprint (`/cards/{cardId}`).")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;
    private final CardSprintService cardSprintService;

    @GetMapping
    public List<SprintResponse> listar() {
        return sprintService.listar();
    }

    @GetMapping("/{id}")
    public SprintResponse buscar(@PathVariable UUID id) {
        return sprintService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SprintResponse criar(@Valid @RequestBody SprintCreateRequest body) {
        return sprintService.criar(body);
    }

    @PutMapping("/{id}")
    public SprintResponse atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody SprintUpdateRequest body) {
        return sprintService.atualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        sprintService.excluir(id);
    }

    @GetMapping("/{sprintId}/cards")
    public List<CardResponse> listarCards(@PathVariable UUID sprintId) {
        return cardSprintService.listarCardsDaSprint(sprintId);
    }

    @PostMapping("/{sprintId}/cards/{cardId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void vincularCard(@PathVariable UUID sprintId, @PathVariable UUID cardId) {
        cardSprintService.vincular(sprintId, cardId);
    }

    @DeleteMapping("/{sprintId}/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desvincularCard(@PathVariable UUID sprintId, @PathVariable UUID cardId) {
        cardSprintService.desvincular(sprintId, cardId);
    }
}
