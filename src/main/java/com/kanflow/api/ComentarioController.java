package com.kanflow.api;

import com.kanflow.api.dto.ComentarioDtos.ComentarioCreateRequest;
import com.kanflow.api.dto.ComentarioDtos.ComentarioResponse;
import com.kanflow.api.dto.ComentarioDtos.ComentarioUpdateRequest;
import com.kanflow.service.ComentarioService;
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
@RequestMapping("/api/cards/{cardId}/comentarios")
@Tag(name = "Comentários", description = "Comentários de um card; criação exige `autorId` de um usuário existente.")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @GetMapping
    public List<ComentarioResponse> listar(@PathVariable UUID cardId) {
        return comentarioService.listarPorCard(cardId);
    }

    @GetMapping("/{comentarioId}")
    public ComentarioResponse buscar(@PathVariable UUID cardId, @PathVariable UUID comentarioId) {
        return comentarioService.buscar(cardId, comentarioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComentarioResponse criar(
            @PathVariable UUID cardId,
            @Valid @RequestBody ComentarioCreateRequest body) {
        return comentarioService.criar(cardId, body);
    }

    @PutMapping("/{comentarioId}")
    public ComentarioResponse atualizar(
            @PathVariable UUID cardId,
            @PathVariable UUID comentarioId,
            @Valid @RequestBody ComentarioUpdateRequest body) {
        return comentarioService.atualizar(cardId, comentarioId, body);
    }

    @DeleteMapping("/{comentarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID cardId, @PathVariable UUID comentarioId) {
        comentarioService.excluir(cardId, comentarioId);
    }
}
