package com.kanflow.repository;

import com.kanflow.domain.entity.Subscription;
import com.kanflow.domain.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findAllByUsuario_IdAndStatus(UUID usuarioId, SubscriptionStatus status);

    Optional<Subscription> findFirstByUsuario_IdAndStatusOrderByCurrentPeriodEndDesc(
            UUID usuarioId, SubscriptionStatus status);
}
