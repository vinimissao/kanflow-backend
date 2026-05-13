package com.kanflow.api;

import com.kanflow.api.dto.BillingDtos.CheckoutRequest;
import com.kanflow.api.dto.BillingDtos.CheckoutResponse;
import com.kanflow.api.dto.BillingDtos.PaymentStatusResponse;
import com.kanflow.api.dto.BillingDtos.PlanStatusResponse;
import com.kanflow.billing.BillingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@Tag(name = "Billing", description = "Plano efetivo, checkout, cancelamento e confirmação mock de pagamento.")
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/plan")
    public PlanStatusResponse plan(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return billingService.planStatus(userId);
    }

    @PostMapping("/checkout")
    public CheckoutResponse checkout(Authentication authentication, @Valid @RequestBody CheckoutRequest body) {
        UUID userId = (UUID) authentication.getPrincipal();
        return billingService.checkout(userId, body.planType(), body.billingPeriod());
    }

    @PostMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    public PlanStatusResponse cancel(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return billingService.cancelSubscription(userId);
    }

    @PostMapping("/payments/{paymentId}/confirm-mock")
    @ResponseStatus(HttpStatus.OK)
    public PaymentStatusResponse confirmMock(Authentication authentication, @PathVariable UUID paymentId) {
        UUID userId = (UUID) authentication.getPrincipal();
        return billingService.confirmMockPayment(userId, paymentId);
    }
}
