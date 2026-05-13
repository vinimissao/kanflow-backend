package com.kanflow.billing;

import com.kanflow.domain.enums.PlanType;

import java.time.Instant;

public record EffectivePlan(
        PlanType planType,
        Integer maxWorkspaces,
        boolean sprintHistoryEnabled,
        boolean showAds,
        Instant currentPeriodEnd
) {
    public static EffectivePlan free() {
        return new EffectivePlan(PlanType.FREE, 1, false, true, null);
    }

    public static EffectivePlan fromPaid(PlanType plan, Instant periodEnd) {
        if (plan == PlanType.FREE) {
            return free();
        }
        return new EffectivePlan(plan, null, true, false, periodEnd);
    }
}
