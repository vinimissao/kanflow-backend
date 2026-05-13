package com.kanflow.billing;

import com.kanflow.domain.enums.BillingPeriod;
import com.kanflow.domain.enums.PlanType;

public final class PlanCatalog {

    private PlanCatalog() {
    }

    public static int amountCents(PlanType plan, BillingPeriod period) {
        if (plan == PlanType.FREE) {
            return 0;
        }
        if (plan == PlanType.BASIC) {
            return period == BillingPeriod.MONTHLY ? 3_900 : 39_000;
        }
        if (plan == PlanType.FULL) {
            return period == BillingPeriod.MONTHLY ? 11_900 : 119_000;
        }
        throw new IllegalArgumentException("Plano inválido: " + plan);
    }
}
