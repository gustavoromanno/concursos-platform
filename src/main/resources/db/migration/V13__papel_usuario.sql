-- Controle de acesso por papel.
--
-- Ate aqui "estar logado" dava direito a tudo, inclusive apagar concursos e
-- videoaulas. Numa URL publica isso significa que qualquer pessoa que se
-- cadastrasse poderia destruir os dados. Agora ha dois papeis:
--   USUARIO -> estuda (responde, comenta, monta cadernos)
--   ADMIN   -> tambem cadastra e remove conteudo do catalogo

ALTER TABLE usuario ADD COLUMN papel VARCHAR(20) NOT NULL DEFAULT 'USUARIO';

-- Promove a conta mais antiga a ADMIN: e a do dono da instalacao.
UPDATE usuario
SET papel = 'ADMIN'
WHERE id = (SELECT MIN(id) FROM usuario);
