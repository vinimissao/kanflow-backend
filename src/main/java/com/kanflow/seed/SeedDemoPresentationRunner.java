package com.kanflow.seed;

import com.kanflow.repository.CardRepository;
import com.kanflow.repository.SprintHistoryRepository;
import com.kanflow.service.SprintHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Profile("seed-demo")
@Order(Integer.MAX_VALUE)
@RequiredArgsConstructor
@Slf4j
public class SeedDemoPresentationRunner implements ApplicationRunner {

    private static final int TARGET_SPRINTS = 15;

    @Value("${kanflow.seed-demo.run:false}")
    private boolean seedDemoRun;

    private final SeedDemoPresentationService seedDemoPresentationService;
    private final SprintHistoryRepository sprintHistoryRepository;
    private final SprintHistoryService sprintHistoryService;
    private final CardRepository cardRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (!seedDemoRun) {
            return;
        }
        seedDemoPresentationService.ensureProfileDemoUsers();
        UUID ownerId = seedDemoPresentationService.ensureUserAndPaidPlan();
        List<SeedDemoPresentationService.TeamMember> team = seedDemoPresentationService.ensureTeam();
        UUID workspaceId = seedDemoPresentationService.ensureDemoWorkspace(ownerId);
        seedDemoPresentationService.ensureDemoWorkspaceMembers(workspaceId);
        long existing = sprintHistoryRepository.countByWorkspace_Id(workspaceId);
        int need = (int) (TARGET_SPRINTS - existing);
        if (need <= 0) {
            log.debug("Seed demo: histórico já tem {} sprints; a saltar criação.", existing);
        } else {
            for (int n = 0; n < need; n++) {
                int sprintOrdinal = (int) existing + n + 1;
                seedDemoPresentationService.createCardsForHistoricalSprint(
                        ownerId, workspaceId, sprintOrdinal, team);
                sprintHistoryService.completeSprint(ownerId, workspaceId);
            }
        }

        int activeSprintLabel = (int) sprintHistoryRepository.countByWorkspace_Id(workspaceId) + 1;
        long onBoard = cardRepository.countByWorkspace_Id(workspaceId);
        if (onBoard > 0) {
            sprintHistoryService.blankBoard(ownerId, workspaceId);
        }
        List<UUID> activeCardIds = seedDemoPresentationService.createCardsForActiveSprint(
                ownerId, workspaceId, activeSprintLabel, team);
        UUID membroId = seedDemoPresentationService.membroUserId();
        seedDemoPresentationService.enrichDemoUserCards(ownerId, membroId, activeCardIds);

        logSummary(workspaceId, need, activeSprintLabel, team);
    }

    private void logSummary(
            UUID workspaceId, int sprintsCreated, int activeSprint, List<SeedDemoPresentationService.TeamMember> team) {
        long sprintsTotal = sprintHistoryRepository.countByWorkspace_Id(workspaceId);
        long cardsBoard = cardRepository.countByWorkspace_Id(workspaceId);
        log.info(
                "Seed demo OK | workspaceId={} | sprints_histórico={} (+{} nesta exec.) | quadro_atual=Sprint {} | cards={}",
                workspaceId,
                sprintsTotal,
                sprintsCreated,
                activeSprint,
                cardsBoard);
        log.info(
                "Perfis demo (senha {}): admin={} | membro={} | visualizador={}",
                SeedDemoPresentationService.DEMO_PASSWORD,
                SeedDemoPresentationService.ADMIN_EMAIL,
                SeedDemoPresentationService.MEMBRO_EMAIL,
                SeedDemoPresentationService.VISUALIZADOR_EMAIL);
        log.info(
                "Colaboradores no quadro ({}): {}",
                team.size(),
                team.stream().map(SeedDemoPresentationService.TeamMember::nome).toList());
    }
}
