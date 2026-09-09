INSERT INTO disciplina (nome) VALUES
    ('Língua Portuguesa'),
    ('Direito Administrativo'),
    ('Raciocínio Lógico');

INSERT INTO banca (nome) VALUES
    ('CESPE/CEBRASPE'),
    ('FGV');

INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, explicacao)
VALUES (
    'A respeito da morfologia da palavra "trem", que abre a segunda quadrinha do poema, pode-se afirmar corretamente que:',
    (SELECT id FROM disciplina WHERE nome = 'Língua Portuguesa'),
    (SELECT id FROM banca WHERE nome = 'CESPE/CEBRASPE'),
    2013,
    'morfologia',
    'A palavra "trem" e um substantivo simples, sem processo de derivacao ou composicao envolvido.'
);

INSERT INTO alternativa (questao_id, texto, correta)
SELECT id, 'é um substantivo simples', TRUE FROM questao WHERE assunto = 'morfologia'
UNION ALL
SELECT id, 'é um substantivo composto', FALSE FROM questao WHERE assunto = 'morfologia'
UNION ALL
SELECT id, 'é um substantivo derivado por prefixação', FALSE FROM questao WHERE assunto = 'morfologia'
UNION ALL
SELECT id, 'é um substantivo derivado por sufixação', FALSE FROM questao WHERE assunto = 'morfologia';
