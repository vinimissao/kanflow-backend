package com.kanflow.api;

import com.kanflow.api.dto.UsuarioDtos.UsuarioCreateRequest;
import com.kanflow.api.dto.UsuarioDtos.UsuarioResponse;
import com.kanflow.api.dto.UsuarioDtos.UsuarioUpdateRequest;
import com.kanflow.security.PerfilAuthorizer;
import com.kanflow.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
    private final PerfilAuthorizer perfilAuthorizer;

    @GetMapping
    public List<UsuarioResponse> listar(Authentication authentication) {
        perfilAuthorizer.assertAdmin((UUID) authentication.getPrincipal());
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(Authentication authentication, @PathVariable UUID id) {
        perfilAuthorizer.assertAdmin((UUID) authentication.getPrincipal());
        return usuarioService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(Authentication authentication, @Valid @RequestBody UsuarioCreateRequest body) {
        perfilAuthorizer.assertAdmin((UUID) authentication.getPrincipal());
        return usuarioService.criar(body);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(
            Authentication authentication, @PathVariable UUID id, @Valid @RequestBody UsuarioUpdateRequest body) {
        perfilAuthorizer.assertAdmin((UUID) authentication.getPrincipal());
        return usuarioService.atualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(Authentication authentication, @PathVariable UUID id) {
        perfilAuthorizer.assertAdmin((UUID) authentication.getPrincipal());
        usuarioService.excluir(id);
    }
}
