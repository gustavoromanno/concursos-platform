-- Corrige o lote da V6, em que a alternativa correta era sempre a primeira.
--
-- A ordem passa a ser guardada em coluna propria. Isso e diferente de
-- embaralhar na hora de exibir: aqui a posicao e sorteada UMA vez e fica fixa,
-- entao a alternativa "A" continua sendo a mesma quando o usuario voltar a
-- questao. Embaralhar a cada exibicao confundiria quem revisa.

ALTER TABLE alternativa ADD COLUMN ordem INTEGER;

-- ROW_NUMBER() com ORDER BY random() dentro de cada questao gera uma
-- permutacao aleatoria independente por questao.
WITH embaralhado AS (
    SELECT id,
           ROW_NUMBER() OVER (PARTITION BY questao_id ORDER BY random()) AS nova_ordem
    FROM alternativa
)
UPDATE alternativa a
SET ordem = e.nova_ordem
FROM embaralhado e
WHERE a.id = e.id;

ALTER TABLE alternativa ALTER COLUMN ordem SET NOT NULL;

CREATE INDEX idx_alternativa_questao_ordem ON alternativa(questao_id, ordem);
