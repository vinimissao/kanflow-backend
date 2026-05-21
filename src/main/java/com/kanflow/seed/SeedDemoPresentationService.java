package com.kanflow.seed;

import com.kanflow.api.dto.CardDtos.CardCreateRequest;
import com.kanflow.api.dto.CardDtos.CardResponse;
import com.kanflow.api.dto.ChecklistDtos.ChecklistItemCreateRequest;
import com.kanflow.api.dto.ComentarioDtos.ComentarioCreateRequest;
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
import com.kanflow.security.WorkspaceAccessService;
import com.kanflow.service.CardService;
import com.kanflow.service.ChecklistItemService;
import com.kanflow.service.ComentarioService;
import com.kanflow.service.UsuarioService;
import com.kanflow.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Profile("seed-demo")
@RequiredArgsConstructor
@Slf4j
public class SeedDemoPresentationService {

    public static final String DEMO_PASSWORD = "Demo2026!";
    public static final String WORKSPACE_NOME = "Kanflow — Demo Apresentação";

    public static final String ADMIN_EMAIL = "admin@kanflow.local";
    public static final String ADMIN_NOME = "Admin Kanflow";
    public static final String MEMBRO_EMAIL = "usuario@kanflow.local";
    public static final String MEMBRO_NOME = "Usuário Membro";
    public static final String VISUALIZADOR_EMAIL = "visualizador@kanflow.local";
    public static final String VISUALIZADOR_NOME = "Visualizador";

    public static final String DEMO_EMAIL = ADMIN_EMAIL;
    public static final String DEMO_NOME = ADMIN_NOME;

    public static final int CARDS_PER_SPRINT = 36;
    private static final int DEMO_USER_ENRICHED_CARDS = 12;

    private static final String[][] TEAM = {
            {"Vinicius", "demo.vinicius@kanflow.local"},
            {"Victor", "demo.victor@kanflow.local"},
            {"Bruno", "demo.bruno@kanflow.local"},
            {"Gustavo", "demo.gustavo@kanflow.local"},
            {"João", "demo.joao@kanflow.local"}
    };

    private static final int[] FIB = {1, 2, 3, 5, 8, 13};
    private static final CardStatus[] ALL_STATUSES = CardStatus.values();
    private static final List<CardStatus> STATUSES_NOT_DONE = Arrays.stream(ALL_STATUSES)
            .filter(s -> s != CardStatus.done)
            .toList();

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final SubscriptionRepository subscriptionRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceService workspaceService;
    private final WorkspaceAccessService workspaceAccessService;
    private final CardService cardService;
    private final ChecklistItemService checklistItemService;
    private final ComentarioService comentarioService;

    public record TeamMember(UUID id, String nome) {}

    @Transactional
    public void ensureProfileDemoUsers() {
        Usuario admin = ensureUser(ADMIN_NOME, ADMIN_EMAIL, PerfilUsuario.admin);
        Usuario membro = ensureUser(MEMBRO_NOME, MEMBRO_EMAIL, PerfilUsuario.membro);
        Usuario visualizador = ensureUser(VISUALIZADOR_NOME, VISUALIZADOR_EMAIL, PerfilUsuario.visualizador);
        grantFullSubscriptionIfNeeded(admin);
        grantFullSubscriptionIfNeeded(membro);
        grantFullSubscriptionIfNeeded(visualizador);
    }

    @Transactional
    public UUID ensureUserAndPaidPlan() {
        Usuario u = ensureUser(ADMIN_NOME, ADMIN_EMAIL, PerfilUsuario.admin);
        grantFullSubscriptionIfNeeded(u);
        return u.getId();
    }

    @Transactional(readOnly = true)
    public UUID membroUserId() {
        return usuarioRepository.findByEmailIgnoreCase(MEMBRO_EMAIL)
                .orElseThrow(() -> new IllegalStateException("Usuário demo membro não encontrado: " + MEMBRO_EMAIL))
                .getId();
    }

    private Usuario ensureUser(String nome, String email, PerfilUsuario perfil) {
        return usuarioRepository
                .findByEmailIgnoreCase(email)
                .map(existing -> {
                    if (existing.getPerfil() != perfil) {
                        existing.setPerfil(perfil);
                        return usuarioRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    var created = usuarioService.criar(new UsuarioCreateRequest(
                            nome, email, DEMO_PASSWORD, perfil));
                    return usuarioRepository.findById(created.id()).orElseThrow();
                });
    }

    @Transactional
    public List<TeamMember> ensureTeam() {
        List<TeamMember> team = new ArrayList<>(TEAM.length);
        for (String[] row : TEAM) {
            String nome = row[0];
            String email = row[1];
            Usuario u = usuarioRepository
                    .findByEmailIgnoreCase(email)
                    .orElseGet(() -> {
                        var created = usuarioService.criar(new UsuarioCreateRequest(
                                nome, email, DEMO_PASSWORD, PerfilUsuario.membro));
                        return usuarioRepository.findById(created.id()).orElseThrow();
                    });
            team.add(new TeamMember(u.getId(), nome));
        }
        return team;
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
        log.debug("Seed demo: assinatura FULL garantida para {}.", u.getEmail());
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
    public void ensureDemoWorkspaceMembers(UUID workspaceId) {
        UUID membroId = usuarioRepository.findByEmailIgnoreCase(MEMBRO_EMAIL)
                .orElseThrow(() -> new IllegalStateException("Usuário demo membro não encontrado: " + MEMBRO_EMAIL))
                .getId();
        UUID visualizadorId = usuarioRepository.findByEmailIgnoreCase(VISUALIZADOR_EMAIL)
                .orElseThrow(() -> new IllegalStateException("Usuário demo visualizador não encontrado: " + VISUALIZADOR_EMAIL))
                .getId();
        workspaceAccessService.ensureMember(workspaceId, membroId);
        workspaceAccessService.ensureMember(workspaceId, visualizadorId);
        log.info(
                "Seed demo: membros do workspace {} -> {}, {}",
                workspaceId,
                MEMBRO_EMAIL,
                VISUALIZADOR_EMAIL);
    }

    @Transactional
    public void createCardsForHistoricalSprint(
            UUID ownerId, UUID workspaceId, int sprintLabel, List<TeamMember> team) {
        createCards(ownerId, workspaceId, sprintLabel, team, false);
    }

    @Transactional
    public List<UUID> createCardsForActiveSprint(
            UUID ownerId, UUID workspaceId, int sprintLabel, List<TeamMember> team) {
        TeamMember admin = teamMemberForDemoUser(ADMIN_EMAIL, ADMIN_NOME);
        TeamMember membro = teamMemberForDemoUser(MEMBRO_EMAIL, MEMBRO_NOME);
        return createCards(ownerId, workspaceId, sprintLabel, team, true, admin, membro);
    }

    @Transactional
    public void enrichDemoUserCards(UUID adminId, UUID membroId, List<UUID> cardIds) {
        int limit = Math.min(DEMO_USER_ENRICHED_CARDS, cardIds.size());
        if (limit == 0) {
            return;
        }
        for (int i = 0; i < limit; i++) {
            UUID cardId = cardIds.get(i);
            boolean adminCard = i % 2 == 0;
            UUID responsavelId = adminCard ? adminId : membroId;
            UUID outroId = adminCard ? membroId : adminId;
            String responsavelNome = adminCard ? ADMIN_NOME : MEMBRO_NOME;
            addChecklistForCard(cardId, i, adminCard);
            comentarioService.criar(cardId, new ComentarioCreateRequest(
                    responsavelId,
                    pickCommentFromOwner(i, responsavelNome)));
            comentarioService.criar(cardId, new ComentarioCreateRequest(
                    outroId,
                    pickCommentFromPeer(i, adminCard)));
        }
        log.info(
                "Seed demo: checklists e comentários em {} cards ({} e {}).",
                limit,
                ADMIN_EMAIL,
                MEMBRO_EMAIL);
    }

    private TeamMember teamMemberForDemoUser(String email, String nome) {
        UUID id = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalStateException("Usuário demo não encontrado: " + email))
                .getId();
        return new TeamMember(id, nome);
    }

    private void addChecklistForCard(UUID cardId, int cardIndex, boolean adminCard) {
        String[][] templates = adminCard ? CHECKLIST_ADMIN : CHECKLIST_MEMBRO;
        String[] items = templates[cardIndex % templates.length];
        for (int j = 0; j < items.length; j++) {
            boolean concluido = j < 2 || (cardIndex + j) % 3 != 0;
            checklistItemService.criar(cardId, new ChecklistItemCreateRequest(items[j], concluido));
        }
    }

    private static final String[][] CHECKLIST_ADMIN = {
            {"Rever critérios de aceite com o PO", "Atualizar documentação técnica", "Validar em ambiente de staging", "Registar evidência na auditoria ISO"},
            {"Confirmar permissões de admin no workspace", "Testar fluxo de conclusão de sprint", "Rever métricas do painel", "Agendar demo com a turma"},
            {"Mapear requisitos ISO 9001 no backlog", "Atualizar roteiro de apresentação", "Validar seed de dados demo", "Fechar action items da retrospectiva"}
    };

    private static final String[][] CHECKLIST_MEMBRO = {
            {"Implementar endpoint no backend", "Escrever testes de integração", "Atualizar Swagger", "Pedir code review"},
            {"Ajustar componente no front", "Testar login como membro", "Verificar drag-and-drop no board", "Corrigir feedback do professor"},
            {"Revisar PR do colega", "Atualizar checklist do card", "Comentar bloqueios no daily", "Submeter evidência no PDCA"}
    };

    private static final String[] COMMENT_FROM_OWNER = {
            "Critérios de aceite alinhados — posso fechar assim que o checklist estiver completo.",
            "Deixei notas no card; qualquer dúvida marquem-me no comentário.",
            "Progresso bom; falta só validação em staging.",
            "Atualizei a descrição conforme feedback da auditoria ISO."
    };

    private static final String[] COMMENT_FROM_PEER = {
            "Revisto do meu lado — sugiro marcar o item de testes antes de mover para Done.",
            "Concordo com o plano; posso ajudar na parte de front se precisarem.",
            "Vi o checklist: 2 itens já concluídos, o resto para esta semana.",
            "Comentário de apoio: documentação no README está consistente com a API."
    };

    private static String pickCommentFromOwner(int cardIndex, String nome) {
        return nome + ": " + COMMENT_FROM_OWNER[cardIndex % COMMENT_FROM_OWNER.length];
    }

    private static String pickCommentFromPeer(int cardIndex, boolean adminCard) {
        String prefix = adminCard ? MEMBRO_NOME : ADMIN_NOME;
        return prefix + ": " + COMMENT_FROM_PEER[cardIndex % COMMENT_FROM_PEER.length];
    }

    private List<UUID> createCards(
            UUID ownerId,
            UUID workspaceId,
            int sprintLabel,
            List<TeamMember> team,
            boolean spreadAcrossColumns,
            TeamMember admin,
            TeamMember membro) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int n = CARDS_PER_SPRINT;
        List<CardStatus> statuses = spreadAcrossColumns
                ? buildSpreadStatuses(n)
                : buildHistoricalStatuses(r, n);
        List<UUID> createdIds = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            int fib = FIB[i % FIB.length];
            int tempo = 1 + (i * 7 + sprintLabel * 3) % 40;
            CardStatus st = statuses.get(i);
            TeamMember member = pickAssignee(team, spreadAcrossColumns, admin, membro, i);
            String titulo = String.format("Sprint %d · Card #%02d", sprintLabel, i + 1);
            String desc = pickDescription(r, i, sprintLabel);
            CardResponse card = cardService.criar(new CardCreateRequest(
                    titulo,
                    desc,
                    fib,
                    tempo,
                    st,
                    member.id(),
                    workspaceId,
                    i + 1,
                    member.nome()));
            createdIds.add(card.id());
        }
        return createdIds;
    }

    private void createCards(
            UUID ownerId,
            UUID workspaceId,
            int sprintLabel,
            List<TeamMember> team,
            boolean spreadAcrossColumns) {
        createCards(ownerId, workspaceId, sprintLabel, team, spreadAcrossColumns, null, null);
    }

    private static TeamMember pickAssignee(
            List<TeamMember> team,
            boolean spreadAcrossColumns,
            TeamMember admin,
            TeamMember membro,
            int index) {
        if (spreadAcrossColumns && admin != null && membro != null && index < DEMO_USER_ENRICHED_CARDS) {
            return index % 2 == 0 ? admin : membro;
        }
        return team.get(index % team.size());
    }

    private static List<CardStatus> buildSpreadStatuses(int n) {
        List<CardStatus> statuses = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            statuses.add(ALL_STATUSES[i % ALL_STATUSES.length]);
        }
        return statuses;
    }

    private static List<CardStatus> buildHistoricalStatuses(ThreadLocalRandom r, int n) {
        int minDone = (n + 1) / 2;
        int doneCount = pickDoneCount(r, n, minDone);
        List<CardStatus> statuses = new ArrayList<>(n);
        for (int k = 0; k < doneCount; k++) {
            statuses.add(CardStatus.done);
        }
        for (int k = doneCount; k < n; k++) {
            statuses.add(STATUSES_NOT_DONE.get(r.nextInt(STATUSES_NOT_DONE.size())));
        }
        Collections.shuffle(statuses, r);
        return statuses;
    }

    private static final String[] DESC_TEMPLATES = {
            "Ajustar validações do formulário de registo e alinhar mensagens de erro com o copy aprovado.",
            "Rever o fluxo de login: expiração de sessão, mensagem de credenciais inválidas e redirecionamento pós-auth.",
            "Implementar listagem de workspaces com ordenação estável e estado de carregamento na UI.",
            "Garantir que a criação de colunas no board persiste ordem e nomes após recarregar a página.",
            "Documentar no README os passos para correr a API com PostgreSQL e com perfil local (H2).",
            "Tratar erros 401/403 no cliente: toast ou banner e retry seguro após renovar token.",
            "Adicionar testes de integração ao endpoint de conclusão de sprint e ao snapshot gravado.",
            "Rever contraste e foco visível nos botões do modal de detalhe do card (acessibilidade).",
            "Implementar pesquisa por texto no workspace com debounce e resultado vazio amigável.",
            "Sincronizar estado do card após PATCH: checklist, responsável e assignee sem dados stale.",
            "Melhorar feedback quando o limite do plano Free bloqueia histórico de sprints ou novo workspace.",
            "Auditar CORS e URLs do Swagger para desenvolvimento em localhost e em IP da rede local.",
            "Definir contrato OpenAPI para billing (checkout, confirmação mock, cancelamento) e exemplos de body.",
            "Otimizar renderização do board com muitos cards: virtualização ou lazy load por coluna.",
            "Corrigir bug em que o contador de checklist não atualiza após marcar o último item como concluído.",
            "Adicionar confirmação antes de concluir sprint, explicando que o quadro atual será arquivado.",
            "Implementar vista de histórico de sprints com resumo de métricas e link para snapshot detalhado.",
            "Rever labels ARIA e ordem de tabulação no modal de card para leitores de ecrã.",
            "Garantir que comentários longos quebram linha e não estouram o layout em mobile.",
            "Adicionar migração Flyway e validação de dados para novos campos de métricas no histórico.",
            "Alinhar front com enums de status do backend (backlog, inDev, codeReview, done, etc.).",
            "Implementar arrastar e largar com alternativa por teclado ou menu contextual no board.",
            "Rever política de passwords: tamanho mínimo, feedback de força e mensagens de registo duplicado.",
            "Preparar dados de demo (seed) com volume realista para testes de performance da listagem.",
            "Documentar variáveis de ambiente (JWT, datasource, porta) para deploy e para Docker Compose.",
            "Tratar race condition ao mover dois cards em sequência rápida para a mesma coluna.",
            "Adicionar logs estruturados no backend para erros 500 com detalhe seguro para o cliente.",
            "Rever textos da app em português: consistência entre \"projeto\", \"workspace\" e \"quadro\".",
            "Implementar cancelamento de assinatura com confirmação e estado visual do plano atualizado.",
            "Garantir que o responsável do card aparece corretamente em listagens e no detalhe após gravação.",
            "Planejar retrospectiva: exportar ou resumir snapshots de sprint para discussão com a equipa."
    };

    private static String pickDescription(ThreadLocalRandom r, int cardIndex, int sprintLabel) {
        int idx = Math.floorMod(cardIndex * 17 + sprintLabel * 11, DESC_TEMPLATES.length);
        String base = DESC_TEMPLATES[idx];
        if (r.nextBoolean()) {
            String extra = DESC_TEMPLATES[Math.floorMod(idx + 3 + r.nextInt(5), DESC_TEMPLATES.length)];
            return base + " " + extra;
        }
        return base;
    }

    private static int pickDoneCount(ThreadLocalRandom r, int n, int minDone) {
        int roll = r.nextInt(100);
        if (roll < 14) {
            return n;
        }
        if (roll < 30) {
            return Math.max(minDone, n - r.nextInt(1, 4));
        }
        if (roll < 48) {
            return Math.max(minDone, n - r.nextInt(4, 9));
        }
        if (roll < 62) {
            return Math.max(minDone, n / 2 + r.nextInt(0, (n - minDone) / 2 + 1));
        }
        return r.nextInt(minDone, n + 1);
    }
}
