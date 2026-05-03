package com.kanflow.service;

import com.kanflow.api.dto.UsuarioDtos.UsuarioCreateRequest;
import com.kanflow.api.dto.UsuarioDtos.UsuarioResponse;
import com.kanflow.api.dto.UsuarioDtos.UsuarioUpdateRequest;
import com.kanflow.api.error.ConflictException;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.entity.Usuario;
import com.kanflow.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(UUID id) {
        return toResponse(usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id)));
    }

    @Transactional
    public UsuarioResponse criar(UsuarioCreateRequest req) {
        if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
            throw new ConflictException("E-mail já cadastrado");
        }
        Usuario u = new Usuario();
        u.setNome(req.nome());
        u.setEmail(req.email().trim().toLowerCase());
        u.setSenhaHash(passwordEncoder.encode(req.senha()));
        u.setPerfil(req.perfil());
        return toResponse(usuarioRepository.save(u));
    }

    @Transactional
    public UsuarioResponse atualizar(UUID id, UsuarioUpdateRequest req) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
        String novoEmail = req.email().trim().toLowerCase();
        if (!novoEmail.equalsIgnoreCase(u.getEmail())
                && usuarioRepository.existsByEmailIgnoreCase(novoEmail)) {
            throw new ConflictException("E-mail já cadastrado");
        }
        u.setNome(req.nome());
        u.setEmail(novoEmail);
        u.setPerfil(req.perfil());
        if (req.senha() != null && !req.senha().isBlank()) {
            u.setSenhaHash(passwordEncoder.encode(req.senha()));
        }
        return toResponse(usuarioRepository.save(u));
    }

    @Transactional
    public void excluir(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.getCriadoEm());
    }
}
