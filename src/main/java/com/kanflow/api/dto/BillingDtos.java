package com.kanflow.api.dto;

import com.kanflow.domain.enums.BillingPeriod;
import com.kanflow.domain.enums.PaymentStatus;
import com.kanflow.domain.enums.PlanType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class BillingDtos {

    private BillingDtos() {
    }

    public record CheckoutRequest(
            @NotNull PlanType planType,
            @NotNull BillingPeriod billingPeriod
    ) {}

    public record CheckoutResponse(
            UUID paymentId,
            int amountCents,
            String currency,
            PlanType planType,
            BillingPeriod billingPeriod,
            PaymentStatus status,
            String hint
    ) {}

    public record PlanStatusResponse(
            PlanType planType,
            Integer maxWorkspaces,
            boolean sprintHistoryEnabled,
            boolean showAds,
            Instant currentPeriodEnd,
            Map<String, Integer> pricesMonthlyCents,
            Map<String, Integer> pricesYearlyCents
    ) {}

    public record PaymentStatusResponse(
            UUID id,
            PlanType planType,
            BillingPeriod billingPeriod,
            int amountCents,
            String currency,
            PaymentStatus status,
            Instant criadoEm,
            Instant paidAt
    ) {}
}
