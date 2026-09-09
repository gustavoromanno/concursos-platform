# Concursos Platform
 
Plataforma de banco de questões para quem estuda para concursos públicos — no estilo do Qconcursos, construída do zero como projeto pessoal.
 
A ideia partiu de uma necessidade real: estudo para concurso e uso esse tipo de plataforma todos os dias. Isso me deu clareza sobre o que realmente importa em um banco de questões — filtrar rápido, receber correção na hora e enxergar onde você está errando mais.
 
## Funcionalidades
 
**Busca de questões com filtros combináveis**
Filtre por disciplina, banca, ano e assunto — em qualquer combinação. Os filtros são montados dinamicamente com JPA Specifications, então não existe um método de repositório para cada combinação possível. Resultados paginados.
 
**Gabarito protegido**
A listagem de questões nunca expõe qual alternativa é a correta. O gabarito e a explicação só aparecem na resposta do endpoint de correção, depois que o usuário escolhe.
 
**Autenticação com JWT**
Cadastro e login com token válido por 24h. Senhas armazenadas com hash BCrypt — nunca em texto puro. Endpoints protegidos identificam o usuário pelo token, então ninguém consegue registrar uma resposta em nome de outra pessoa.
 
**Correção imediata com histórico**
Ao responder, o usuário recebe na hora se acertou, qual era a alternativa correta e a explicação. Cada resposta fica gravada e vinculada à conta, formando o histórico que alimenta as estatísticas de desempenho.
 
## Stack
 
| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.3 |
| Persistência | Spring Data JPA / Hibernate |
| Banco de dados | PostgreSQL |
| Migrations | Flyway |
| Segurança | Spring Security + JWT (jjwt) |
| Build | Maven |
 
## Decisões de projeto
 
**Schema versionado desde o primeiro commit.** O Hibernate roda em modo `validate`, e todas as mudanças de estrutura passam por migrations do Flyway. Isso mantém o banco reproduzível e evita alterações silenciosas de schema.
 
**Entidades não são expostas na API.** Toda entrada e saída passa por DTOs. Além de desacoplar o modelo de persistência do contrato público, é isso que permite omitir o gabarito na listagem sem gambiarra.
 
**Credenciais fora do código.** URL do banco, usuário, senha e o segredo do JWT vêm de variáveis de ambiente. O repositório não contém nenhuma credencial.
 
**Sessão stateless.** Sem sessão em servidor: cada requisição carrega seu próprio token. `spring.jpa.open-in-view` desabilitado, com as fronteiras transacionais explícitas onde são necessárias.
 
## Modelo de dados
 
```
Disciplina ──┐
             ├──< Questao ──< Alternativa
Banca ───────┘       │            │
                     │            │
Usuario ──────────< Resposta >────┘
```
 
Uma resposta liga um usuário, uma questão e a alternativa escolhida — a base para calcular acerto por disciplina, por banca ou por período.
 
## API
 
### Público
 
```http
POST /auth/registrar
{ "nome": "Fulano", "email": "fulano@exemplo.com", "senha": "senha123" }
```
 
```http
POST /auth/login
{ "email": "fulano@exemplo.com", "senha": "senha123" }
```
Retorna o token JWT a ser usado nas chamadas protegidas.
 
### Autenticado
 
Requer o header `Authorization: Bearer <token>`.
 
```http
GET /questoes?disciplinaId=1&bancaId=1&ano=2013&assunto=morfologia&page=0&size=20
```
Todos os parâmetros são opcionais e combináveis.
 
```http
POST /questoes/{id}/responder
{ "alternativaId": 2 }
```
```json
{
  "correta": false,
  "alternativaCorretaId": 1,
  "explicacao": "..."
}
```
 
## Rodando localmente
 
**Pré-requisitos:** JDK 21, Maven 3.9+ e uma instância PostgreSQL (local ou gerenciada).
 
Configure as variáveis de ambiente:
 
```
DB_URL       = jdbc:postgresql://host/banco
DB_USERNAME  = usuario
DB_PASSWORD  = senha
JWT_SECRET   = string-aleatoria-com-no-minimo-32-caracteres
```
 
```bash
mvn spring-boot:run
```
 
As tabelas são criadas automaticamente pelo Flyway na primeira execução, junto com um seed de dados para teste imediato.
 
## Roadmap
 
- [x] Modelagem de dados e schema versionado
- [x] Busca de questões com filtros combináveis e paginação
- [x] Autenticação JWT e correção de questões
- [ ] Dashboard de desempenho (% de acerto por disciplina, evolução no tempo)
- [ ] Refazer questões erradas
- [ ] Simulado cronometrado
- [ ] Interface web
## Autor
 
**Gustavo Romano**
[LinkedIn](https://linkedin.com/in/gustavoromanno) · [GitHub](https://github.com/gustavoromanno)
 
