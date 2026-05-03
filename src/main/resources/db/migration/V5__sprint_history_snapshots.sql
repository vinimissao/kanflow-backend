CREATE TABLE sprint_history (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES workspace (id) ON DELETE CASCADE,
    numero INTEGER NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    ended_at TIMESTAMPTZ NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (workspace_id, numero)
);

CREATE TABLE sprint_snapshot (
    id UUID PRIMARY KEY,
    sprint_history_id UUID NOT NULL REFERENCES sprint_history (id) ON DELETE CASCADE,
    snapshot_json TEXT NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (sprint_history_id)
);

CREATE INDEX idx_sprint_history_workspace ON sprint_history (workspace_id);

