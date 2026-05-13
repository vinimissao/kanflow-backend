package com.kanflow.billing;

import com.kanflow.api.dto.BillingDtos.CheckoutResponse;
import com.kanflow.api.dto.BillingDtos.PaymentStatusResponse;
import com.kanflow.api.dto.BillingDtos.PlanStatusResponse;
import com.kanflow.api.error.ConflictException;
import com.kanflow.api.error.PlanLimitException;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.entity.Payment;
import com.kanflow.domain.entity.Subscription;
import com.kanflow.domain.entity.Usuario;
import com.kanflow.domain.enums.BillingPeriod;
import com.kanflow.domain.enums.PaymentProvider;
import com.kanflow.domain.enums.PaymentStatus;
import com.kanflow.domain.enums.PlanType;
import com.kanflow.domain.enums.SubscriptionStatus;
import com.kanflow.repository.PaymentRepository;
import com.kanflow.repository.SubscriptionRepository;
import com.kanflow.repository.UsuarioRepository;
import com.kanflow.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final UsuarioRepository usuarioRepository;
    private final WorkspaceRepository workspaceRepository;

    @Value("${kanflow.billing.mock-confirmation:true}")
    private boolean mockConfirmationEnabled;

    @Transactional(readOnly = true)
    public EffectivePlan effectivePlan(UUID usuarioId) {
        Instant now = Instant.now();
        return subscriptionRepository
                .findFirstByUsuario_IdAndStatusOrderByCurrentPeriodEndDesc(usuarioId, SubscriptionStatus.ACTIVE)
                .filter(s -> !s.getCurrentPeriodEnd().isBefore(now))
                .map(s -> EffectivePlan.fromPaid(s.getPlanType(), s.getCurrentPeriodEnd()))
                .orElse(EffectivePlan.free());
    }

    @Transactional(readOnly = true)
    public PlanStatusResponse planStatus(UUID usuarioId) {
        EffectivePlan p = effectivePlan(usuarioId);
        return new PlanStatusResponse(
                p.planType(),
                p.maxWorkspaces(),
                p.sprintHistoryEnabled(),
                p.showAds(),
                p.currentPeriodEnd(),
                monthlyPrices(),
                yearlyPrices()
        );
    }

    private static Map<String, Integer> monthlyPrices() {
        return Map.of(
                PlanType.BASIC.name(), PlanCatalog.amountCents(PlanType.BASIC, BillingPeriod.MONTHLY),
                PlanType.FULL.name(), PlanCatalog.amountCents(PlanType.FULL, BillingPeriod.MONTHLY)
        );
    }

    private static Map<String, Integer> yearlyPrices() {
        return Map.of(
                PlanType.BASIC.name(), PlanCatalog.amountCents(PlanType.BASIC, BillingPeriod.YEARLY),
                PlanType.FULL.name(), PlanCatalog.amountCents(PlanType.FULL, BillingPeriod.YEARLY)
        );
    }

    public void assertCanCreateWorkspace(UUID ownerId) {
        EffectivePlan p = effectivePlan(ownerId);
        if (p.maxWorkspaces() == null) {
            return;
        }
        long count = workspaceRepository.countByOwner_Id(ownerId);
        if (count >= p.maxWorkspaces()) {
            throw new PlanLimitException(
                    "Limite de projetos atingido no plano " + p.planType()
                            + ". Faça upgrade para criar mais workspaces.");
        }
    }

    public void assertSprintHistoryWrite(UUID ownerId) {
        if (!effectivePlan(ownerId).sprintHistoryEnabled()) {
            throw new PlanLimitException(
                    "Histórico de sprints não está disponível no plano Free. Assine Básico ou Full.");
        }
    }

    public boolean sprintHistoryReadable(UUID ownerId) {
        return effectivePlan(ownerId).sprintHistoryEnabled();
    }

    @Transactional
    public CheckoutResponse checkout(UUID usuarioId, PlanType planType, BillingPeriod billingPeriod) {
        if (planType == PlanType.FREE) {
            throw new ConflictException("O plano Free não requer checkout.");
        }
        Usuario u = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + usuarioId));
        int cents = PlanCatalog.amountCents(planType, billingPeriod);
        Payment p = new Payment();
        p.setUsuario(u);
        p.setPlanType(planType);
        p.setBillingPeriod(billingPeriod);
        p.setAmountCents(cents);
        p.setCurrency("BRL");
        p.setStatus(PaymentStatus.PENDING);
        p.setProvider(PaymentProvider.MOCK);
        paymentRepository.save(p);
        String hint = mockConfirmationEnabled
                ? "Dev: confirme com POST /api/billing/payments/{id}/confirm-mock"
                : "Aguarde integração de pagamento (webhook).";
        return new CheckoutResponse(
                p.getId(),
                cents,
                "BRL",
                planType,
                billingPeriod,
                PaymentStatus.PENDING,
                hint
        );
    }

    @Transactional
    public PaymentStatusResponse confirmMockPayment(UUID usuarioId, UUID paymentId) {
        if (!mockConfirmationEnabled) {
            throw new ConflictException("Confirmação simulada desligada (kanflow.billing.mock-confirmation).");
        }
        Payment p = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + paymentId));
        if (!p.getUsuario().getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Pagamento não encontrado: " + paymentId);
        }
        if (p.getStatus() != PaymentStatus.PENDING) {
            throw new ConflictException("Pagamento não está pendente.");
        }
        p.setStatus(PaymentStatus.PAID);
        p.setPaidAt(Instant.now());
        paymentRepository.save(p);

        Instant start = Instant.now();
        Instant end = p.getBillingPeriod() == BillingPeriod.MONTHLY
                ? start.plus(30, ChronoUnit.DAYS)
                : start.plus(365, ChronoUnit.DAYS);

        List<Subscription> active = subscriptionRepository.findAllByUsuario_IdAndStatus(usuarioId, SubscriptionStatus.ACTIVE);
        for (Subscription s : active) {
            s.setStatus(SubscriptionStatus.CANCELLED);
        }
        subscriptionRepository.saveAll(active);

        Subscription sub = new Subscription();
        sub.setUsuario(p.getUsuario());
        sub.setPlanType(p.getPlanType());
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setCurrentPeriodStart(start);
        sub.setCurrentPeriodEnd(end);
        subscriptionRepository.save(sub);

        return new PaymentStatusResponse(
                p.getId(),
                p.getPlanType(),
                p.getBillingPeriod(),
                p.getAmountCents(),
                p.getCurrency(),
                p.getStatus(),
                p.getCriadoEm(),
                p.getPaidAt()
        );
    }

    @Transactional
    public PlanStatusResponse cancelSubscription(UUID usuarioId) {
        Instant now = Instant.now();
        List<Subscription> active = subscriptionRepository.findAllByUsuario_IdAndStatus(usuarioId, SubscriptionStatus.ACTIVE)
                .stream()
                .filter(s -> !s.getCurrentPeriodEnd().isBefore(now))
                .toList();
        if (active.isEmpty()) {
            throw new ConflictException("Não há assinatura ativa para cancelar.");
        }
        for (Subscription s : active) {
            s.setStatus(SubscriptionStatus.CANCELLED);
        }
        subscriptionRepository.saveAll(active);
        return planStatus(usuarioId);
    }
}
