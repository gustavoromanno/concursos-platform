-- Registros de acesso a aplicacao (Marco Civil da Internet, Lei 12.965/2014, art. 15):
-- data, hora e IP de cada login, guardados por 6 meses e depois apagados automaticamente.
--
-- Sem chave estrangeira de proposito: a guarda e obrigacao legal e continua valendo
-- mesmo se a pessoa excluir a conta (LGPD art. 16, I). Ninguem le esta tabela pelo site;
-- ela so serve para atender ordem judicial.
CREATE TABLE registro_acesso (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    ip VARCHAR(255) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_registro_acesso_criado ON registro_acesso(criado_em);
CREATE INDEX idx_registro_acesso_usuario ON registro_acesso(usuario_id, criado_em DESC);
