CREATE TABLE workspace (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    nome VARCHAR(255) NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE board_column (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES workspace (id) ON DELETE CASCADE,
    nome VARCHAR(120) NOT NULL,
    ordem INTEGER NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (workspace_id, ordem)
);

CREATE INDEX idx_workspace_owner ON workspace (owner_id);
CREATE INDEX idx_board_column_workspace ON board_column (workspace_id);

