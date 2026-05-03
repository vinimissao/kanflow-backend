ALTER TABLE card
    ADD COLUMN workspace_id UUID REFERENCES workspace (id) ON DELETE CASCADE;

ALTER TABLE card
    ADD COLUMN posicao INTEGER;

ALTER TABLE card
    ADD COLUMN assignee VARCHAR(255);

CREATE INDEX idx_card_workspace ON card (workspace_id);
CREATE INDEX idx_card_workspace_status ON card (workspace_id, status);

