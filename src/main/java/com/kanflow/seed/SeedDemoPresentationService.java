package com.kanflow.seed;

import com.kanflow.api.dto.CardDtos.CardCreateRequest;
import com.kanflow.api.dto.UsuarioDtos.UsuarioCreateRequest;
import com.kanflow.api.dto.WorkspaceDtos.WorkspaceCreateRequest;
import com.kanflow.domain.entity.Subscription;
import com.kanflow.domain.entity.Usuario;
import com.kanflow.domain.enums.CardStatus;
import com.kanflow.domain.enums.PerfilUsuario;
import com.kanflow.domain.enums.PlanType;
import com.kanflow.domain.enums.SubscriptionStatus;
import com.kanflow.repository.SubscriptionRepository;
import com.kanflow.repository.UsuarioRepository;
import com.kanflow.repository.WorkspaceRepository;
import com.kanflow.service.CardService;
import com.kanflow.service.UsuarioService;
import com.kanflow.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Profile("seed-demo")
@RequiredArgsConstructor
@Slf4j
public class SeedDemoPresentationService {

    public static final String DEMO_EMAIL = "demo.apresentacao@kanflow.local";
    public static final String DEMO_PASSWORD = "Demo2026!";
    public static final String DEMO_NOME = "Usuário Demo Apresentação";
    public static final String WORKSPACE_NOME = "Kanflow — Demo Apresentação";

    public static final int CARDS_PER_SPRINT = 36;
    private static final int[] FIB = {1, 2, 3, 5, 8, 13};
    private static final CardStatus[] STATUSES = CardStatus.values();

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final SubscriptionRepository subscriptionRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceService workspaceService;
    private final CardService cardService;

    @Transactional
    public UUID ensureUserAndPaidPlan() {
        Usuario u = usuarioRepository
                .findByEmailIgnoreCase(DEMO_EMAIL)
                .orElseGet(() -> {
                    var created = usuarioService.criar(new UsuarioCreateRequest(
                            DEMO_NOME, DEMO_EMAIL, DEMO_PASSWORD, PerfilUsuario.membro));
                    return usuarioRepository.findById(created.id()).orElseThrow();
                });
        grantFullSubscriptionIfNeeded(u);
        return u.getId();
    }

    private void grantFullSubscriptionIfNeeded(Usuario u) {
        Instant now = Instant.now();
        var current = subscriptionRepository.findFirstByUsuario_IdAndStatusOrderByCurrentPeriodEndDesc(
                u.getId(), SubscriptionStatus.ACTIVE);
        if (current.isPresent()) {
            Subscription s = current.get();
            if (!s.getCurrentPeriodEnd().isBefore(now)
                    && s.getPlanType() != PlanType.FREE
                    && (s.getPlanType() == PlanType.FULL || s.getPlanType() == PlanType.BASIC)) {
                return;
            }
        }
        List<Subscription> active = subscriptionRepository.findAllByUsuario_IdAndStatus(u.getId(), SubscriptionStatus.ACTIVE);
        for (Subscription s : active) {
            s.setStatus(SubscriptionStatus.CANCELLED);
        }
        subscriptionRepository.saveAll(active);
        Subscription sub = new Subscription();
        sub.setUsuario(u);
        sub.setPlanType(PlanType.FULL);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setCurrentPeriodStart(now);
        sub.setCurrentPeriodEnd(now.plus(365, ChronoUnit.DAYS));
        subscriptionRepository.save(sub);
        log.info("Seed demo: assinatura FULL ativa garantida para {}.", u.getEmail());
    }

    @Transactional
    public UUID ensureDemoWorkspace(UUID ownerId) {
        return workspaceRepository.findAllByOwnerIdOrderByCriadoEmAsc(ownerId).stream()
                .filter(w -> WORKSPACE_NOME.equals(w.getNome()))
                .findFirst()
                .map(w -> w.getId())
                .orElseGet(() -> workspaceService
                        .create(ownerId, new WorkspaceCreateRequest(WORKSPACE_NOME, null))
                        .id());
    }

    @Transactional
    public void createCardsForCurrentBoard(UUID ownerId, UUID workspaceId, int sprintLabel) {
        for (int i = 0; i < CARDS_PER_SPRINT; i++) {
            int fib = FIB[i % FIB.length];
            int tempo = 1 + (i * 7 + sprintLabel * 3) % 40;
            CardStatus st = STATUSES[(i + sprintLabel) % STATUSES.length];
            String titulo = String.format("Sprint %d · Card #%02d · %d pts", sprintLabel, i + 1, fib);
            String desc = String.format(
                    "Dados sintéticos para demo. Pontos=%d, tempo estimado=%dh, responsável definido.", fib, tempo);
            cardService.criar(new CardCreateRequest(
                    titulo,
                    desc,
                    fib,
                    tempo,
                    st,
                    ownerId,
                    workspaceId,
                    i + 1,
                    DEMO_NOME));
        }
    }
}
