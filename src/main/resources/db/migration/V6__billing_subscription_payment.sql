CREATE TABLE subscription (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuario (id),
    plan_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    current_period_start TIMESTAMPTZ NOT NULL,
    current_period_end TIMESTAMPTZ NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_subscription_usuario_status ON subscription (usuario_id, status);

CREATE TABLE payment (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuario (id),
    plan_type VARCHAR(32) NOT NULL,
    billing_period VARCHAR(32) NOT NULL,
    amount_cents INTEGER NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    status VARCHAR(32) NOT NULL,
    provider VARCHAR(32) NOT NULL,
    external_ref VARCHAR(255),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    paid_at TIMESTAMPTZ
);

CREATE INDEX idx_payment_usuario ON payment (usuario_id);
