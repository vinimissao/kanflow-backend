-- Planning poker Fibonacci: 1, 2, 3, 5, 8, 13 (substitui dificuldade Baixa/Médio/Alta)
ALTER TABLE card ADD COLUMN pontos INTEGER;

UPDATE card
SET pontos = CASE dificuldade
    WHEN 'Baixa' THEN 1
    WHEN 'Medio' THEN 3
    WHEN 'Alta' THEN 8
    ELSE 3
    END;

ALTER TABLE card ALTER COLUMN pontos SET NOT NULL;
ALTER TABLE card DROP COLUMN dificuldade;
