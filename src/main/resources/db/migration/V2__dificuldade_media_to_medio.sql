-- Constante Java renomeada de Media -> Medio (evita conflito de nome em alguns IDEs).
UPDATE card SET dificuldade = 'Medio' WHERE dificuldade = 'Media';
