-- Primeiras videoaulas da biblioteca: videos publicos do YouTube, conferidos um a um
-- (titulo e canal). O site so incorpora o player; o conteudo continua no YouTube.
-- Cada linha so entra se o assunto existir e o video ainda nao estiver cadastrado.
-- As proximas entram pela busca do admin (Videoaulas > Buscar no YouTube).
INSERT INTO videoaula (titulo, youtube_id, canal, duracao_minutos, disciplina_id, assunto_id, criado_em)
SELECT v.titulo, v.youtube_id, v.canal, v.duracao, d.id, a.id, NOW()
FROM (VALUES
    ('Concordância Verbal - Aula 01', '4ZJnTqTk4_Y', 'Professor Noslen', NULL,
     'Língua Portuguesa', 'Concordância verbal'),
    ('Concordância Nominal - Aula 02', 'ISPEMEtMprA', 'Professor Noslen', NULL,
     'Língua Portuguesa', 'Concordância nominal'),
    ('Pronomes oblíquos: quando utilizar e como cai em concurso', 'g2mfk4xFOOU', 'Prof. Álvaro Ferreira', NULL,
     'Língua Portuguesa', 'Colocação pronominal'),
    ('Estrutura do Sistema Financeiro Nacional (curso Banco do Brasil)', 'QYINo30yDvA', 'Prof. Renan Duarte', 67,
     'Conhecimentos Bancários', 'Sistema Financeiro Nacional'),
    ('Sistema Financeiro Nacional (SFN) - curso Caixa', 'GQh5dJjCuFc', 'Capriata Educação', NULL,
     'Conhecimentos Bancários', 'Sistema Financeiro Nacional'),
    ('COPOM e operações compromissadas', '_8GJ1EGv0h0', 'Capriata Educação', NULL,
     'Conhecimentos Bancários', 'Política monetária')
) AS v(titulo, youtube_id, canal, duracao, disciplina, assunto)
JOIN disciplina d ON d.nome = v.disciplina
JOIN assunto a ON a.disciplina_id = d.id AND a.nome = v.assunto
WHERE NOT EXISTS (SELECT 1 FROM videoaula x WHERE x.youtube_id = v.youtube_id);
