package com.kanflow.api;

import com.kanflow.api.dto.UsuarioDtos.UsuarioCreateRequest;
import com.kanflow.api.dto.UsuarioDtos.UsuarioResponse;
import com.kanflow.api.dto.UsuarioDtos.UsuarioUpdateRequest;
import com.kanflow.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "CRUD de usuários. Senha persistida com BCrypt; respostas não expõem hash.")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable UUID id) {
        return usuarioService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@Valid @RequestBody UsuarioCreateRequest body) {
        return usuarioService.criar(body);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable UUID id, @Valid @RequestBody UsuarioUpdateRequest body) {
        return usuarioService.atualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        usuarioService.excluir(id);
    }
}
