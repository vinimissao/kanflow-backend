CREATE TABLE workspace_member (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES workspace (id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (workspace_id, usuario_id)
);

CREATE INDEX idx_workspace_member_usuario ON workspace_member (usuario_id);
CREATE INDEX idx_workspace_member_workspace ON workspace_member (workspace_id);
