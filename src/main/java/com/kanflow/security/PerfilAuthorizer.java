package com.kanflow.security;

import com.kanflow.api.error.ForbiddenOperationException;
import com.kanflow.domain.entity.Usuario;
import com.kanflow.domain.enums.PerfilUsuario;
import com.kanflow.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PerfilAuthorizer {

    private final UsuarioRepository usuarioRepository;

    public Usuario requireUser(UUID userId) {
        return usuarioRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenOperationException("Utilizador não encontrado."));
    }

    public PerfilUsuario requirePerfil(UUID userId) {
        return requireUser(userId).getPerfil();
    }

    public void assertCanWrite(UUID userId) {
        PerfilUsuario p = requirePerfil(userId);
        if (p == PerfilUsuario.visualizador) {
            throw new ForbiddenOperationException(
                    "Perfil visualizador: apenas consulta. Não pode criar ou alterar dados.");
        }
    }

    public void assertAdmin(UUID userId) {
        if (requirePerfil(userId) != PerfilUsuario.admin) {
            throw new ForbiddenOperationException("Apenas administradores podem executar esta ação.");
        }
    }
}
