CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE card (
    id UUID PRIMARY KEY,
    titulo VARCHAR(500) NOT NULL,
    descricao TEXT,
    dificuldade VARCHAR(20) NOT NULL,
    tempo_estimado INTEGER,
    status VARCHAR(30) NOT NULL,
    responsavel_id UUID REFERENCES usuario (id),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE checklist_item (
    id UUID PRIMARY KEY,
    card_id UUID NOT NULL REFERENCES card (id) ON DELETE CASCADE,
    texto VARCHAR(2000) NOT NULL,
    concluido BOOLEAN NOT NULL DEFAULT false,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE comentario (
    id UUID PRIMARY KEY,
    card_id UUID NOT NULL REFERENCES card (id) ON DELETE CASCADE,
    autor_id UUID NOT NULL REFERENCES usuario (id),
    texto TEXT NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE sprint (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    status VARCHAR(30) NOT NULL
);

CREATE TABLE card_sprint (
    id UUID PRIMARY KEY,
    card_id UUID NOT NULL REFERENCES card (id) ON DELETE CASCADE,
    sprint_id UUID NOT NULL REFERENCES sprint (id) ON DELETE CASCADE,
    UNIQUE (card_id, sprint_id)
);

CREATE INDEX idx_card_responsavel ON card (responsavel_id);
CREATE INDEX idx_checklist_item_card ON checklist_item (card_id);
CREATE INDEX idx_comentario_card ON comentario (card_id);
CREATE INDEX idx_comentario_autor ON comentario (autor_id);
CREATE INDEX idx_card_sprint_sprint ON card_sprint (sprint_id);
