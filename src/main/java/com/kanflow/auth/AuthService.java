package com.kanflow.auth;

import com.kanflow.api.error.ConflictException;
import com.kanflow.api.error.ForbiddenOperationException;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.enums.PerfilUsuario;
import com.kanflow.auth.AuthDtos.AuthResponse;
import com.kanflow.auth.AuthDtos.ChangePasswordRequest;
import com.kanflow.auth.AuthDtos.LoginRequest;
import com.kanflow.auth.AuthDtos.MeResponse;
import com.kanflow.auth.AuthDtos.RegisterRequest;
import com.kanflow.domain.entity.Usuario;
import com.kanflow.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${security.jwt.access-token-seconds:3600}")
    private long accessTokenSeconds;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("E-mail já cadastrado");
        }
        if (req.perfil() == PerfilUsuario.admin) {
            throw new ForbiddenOperationException(
                    "Registo público não permite perfil admin. Use a conta de administrador de demonstração ou peça a um admin.");
        }
        Usuario u = new Usuario();
        u.setNome(req.nome().trim());
        u.setEmail(email);
        u.setSenhaHash(passwordEncoder.encode(req.senha()));
        u.setPerfil(req.perfil());
        Usuario saved = usuarioRepository.save(u);
        String token = jwtService.generateAccessToken(saved.getId(), saved.getEmail());
        return new AuthResponse(token, "Bearer", accessTokenSeconds);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        Usuario u = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Credenciais inválidas"));
        if (!passwordEncoder.matches(req.senha(), u.getSenhaHash())) {
            throw new ResourceNotFoundException("Credenciais inválidas");
        }
        String token = jwtService.generateAccessToken(u.getId(), u.getEmail());
        return new AuthResponse(token, "Bearer", accessTokenSeconds);
    }

    @Transactional(readOnly = true)
    public MeResponse me(UUID userId) {
        Usuario u = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return new MeResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.getCriadoEm());
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest req) {
        Usuario u = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        if (!passwordEncoder.matches(req.senhaAtual(), u.getSenhaHash())) {
            throw new ResourceNotFoundException("Senha atual inválida");
        }
        u.setSenhaHash(passwordEncoder.encode(req.novaSenha()));
        usuarioRepository.save(u);
    }
}

