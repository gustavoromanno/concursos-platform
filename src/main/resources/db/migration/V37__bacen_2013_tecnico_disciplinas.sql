-- Bacen 2013, cargo de Tecnico: conteudo programatico que faltava na V34.
--
-- Fonte: Edital nº 1/2013 BCB/DEPES, de 15/08/2013, item 22.2.3 (Conhecimentos Basicos
-- para o cargo de Tecnico) e item 22.2.4 (Conhecimentos Especificos), lidos na integra.
-- Mapeamento para as disciplinas do banco:
--   Lingua Portuguesa                 -> Língua Portuguesa
--   Nocoes de Direito Constitucional  -> Direito Constitucional
--   Nocoes de Direito Administrativo  -> Direito Administrativo
--   Gestao Publica                    -> Administração Pública
--   Informatica para Usuarios         -> Noções de Informática
--   Raciocinio Logico-Quantitativo    -> Raciocínio Lógico
--   Area 1: Fundamentos de Contabilidade        -> Contabilidade
--           Fundamentos de Gestao de Pessoas    -> Gestão de Pessoas (nova)
--           Fundamentos de Gestao de Rec. Mat.  -> Administração de Recursos Materiais (nova)
--   Area 2: os tres blocos (gestao de riscos e inteligencia; seguranca fisica, operacional e
--           publica; fiscalizacao de contratos de seguranca) nao tem disciplina equivalente e
--           ficam so nas observacoes.

INSERT INTO disciplina (nome)
SELECT v.nome FROM (VALUES ('Gestão de Pessoas'), ('Administração de Recursos Materiais')) AS v(nome)
WHERE NOT EXISTS (SELECT 1 FROM disciplina d WHERE d.nome = v.nome);

INSERT INTO cargo_disciplina (cargo_id, disciplina_id, total_topicos, ordem)
SELECT cc.id, d.id, NULL, v.ordem
FROM (VALUES
    ('Técnico – Área %', 'Língua Portuguesa', 1),
    ('Técnico – Área %', 'Direito Constitucional', 2),
    ('Técnico – Área %', 'Direito Administrativo', 3),
    ('Técnico – Área %', 'Administração Pública', 4),
    ('Técnico – Área %', 'Noções de Informática', 5),
    ('Técnico – Área %', 'Raciocínio Lógico', 6),
    ('Técnico – Área 1:%', 'Contabilidade', 7),
    ('Técnico – Área 1:%', 'Gestão de Pessoas', 8),
    ('Técnico – Área 1:%', 'Administração de Recursos Materiais', 9)
) AS v(cargo, disciplina, ordem)
JOIN concurso c ON LOWER(c.nome) = 'bacen 2013' AND c.ano = 2013
JOIN concurso_cargo cc ON cc.concurso_id = c.id AND cc.nome LIKE v.cargo
JOIN disciplina d ON d.id = (SELECT MIN(id) FROM disciplina WHERE nome = v.disciplina)
WHERE NOT EXISTS (SELECT 1 FROM cargo_disciplina x WHERE x.cargo_id = cc.id AND x.disciplina_id = d.id);

-- Troca, nas observacoes, o aviso de "nao conferido" pelo conteudo do edital.
-- REPLACE so age se o texto antigo ainda estiver la: rodar de novo nao muda nada.
UPDATE concurso
SET observacoes = REPLACE(observacoes,
    'Técnico: as disciplinas (item 22 do edital) não puderam ser conferidas no edital oficial e ficaram fora do conteúdo programático.',
    'Técnico (item 22): básicos comuns às duas áreas – Língua Portuguesa, Noções de Dir. Constitucional, '
 || 'Noções de Dir. Administrativo, Gestão Pública (com Ética no Serviço Público), Informática para Usuários e '
 || 'Raciocínio Lógico-Quantitativo. Específicos da Área 1: Fundamentos de Contabilidade, de Gestão de Pessoas e '
 || 'de Gestão de Recursos Materiais. Específicos da Área 2: Gestão de Riscos, Continuidade de Negócios e '
 || 'Inteligência; Segurança Física, Operacional e Pública; Noções de Fiscalização de Contratos Terceirizados '
 || 'relativos à Segurança.')
WHERE LOWER(nome) = 'bacen 2013' AND ano = 2013
  AND observacoes LIKE '%Técnico: as disciplinas (item 22 do edital) não puderam ser conferidas%';
