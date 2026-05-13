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
            log.info("Seed demo: kanflow.seed-demo.run=false — ignorado.");
            return;
        }
        UUID ownerId = seedDemoPresentationService.ensureUserAndPaidPlan();
        UUID workspaceId = seedDemoPresentationService.ensureDemoWorkspace(ownerId);
        long existing = sprintHistoryRepository.countByWorkspace_Id(workspaceId);
        int need = (int) (TARGET_SPRINTS - existing);
        if (need <= 0) {
            log.info(
                    "Seed demo: workspace {} já tem {} sprints no histórico (mínimo {}). A saltar criação de sprints.",
                    workspaceId,
                    existing,
                    TARGET_SPRINTS);
        } else {
            log.info("Seed demo: a criar {} sprint(s) com cards em {}…", need, SeedDemoPresentationService.WORKSPACE_NOME);
            for (int n = 0; n < need; n++) {
                int sprintOrdinal = (int) existing + n + 1;
                seedDemoPresentationService.createCardsForCurrentBoard(ownerId, workspaceId, sprintOrdinal);
                sprintHistoryService.completeSprint(ownerId, workspaceId);
                if ((n + 1) % 5 == 0 || n == need - 1) {
                    log.info("Seed demo: concluídas {} / {} sprints nesta execução.", n + 1, need);
                }
            }
        }

        long onBoard = cardRepository.countByWorkspace_Id(workspaceId);
        if (onBoard == 0) {
            int quadroLabel = (int) sprintHistoryRepository.countByWorkspace_Id(workspaceId) + 1;
            seedDemoPresentationService.createCardsForCurrentBoard(ownerId, workspaceId, quadroLabel);
            long after = cardRepository.countByWorkspace_Id(workspaceId);
            log.info(
                    "Seed demo: quadro atual populado com {} cards (sprint {} no título).",
                    after,
                    quadroLabel);
        } else {
            log.info("Seed demo: quadro já tinha {} card(s); não foram criados cards extra.", onBoard);
        }

        logSummary(workspaceId);
    }

    private void logSummary(UUID workspaceId) {
        log.info("---");
        log.info("Demo: email={} senha={}", SeedDemoPresentationService.DEMO_EMAIL, SeedDemoPresentationService.DEMO_PASSWORD);
        log.info("WorkspaceId={}", workspaceId);
        log.info("---");
    }
}
