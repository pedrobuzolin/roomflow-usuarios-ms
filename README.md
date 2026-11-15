# usuarios-ms

Microsserviço de usuários do [Roomflow](https://github.com/pedrobuzolin/roomflow-microsservicos), um sistema de reserva de salas. Para rodar o conjunto completo, use o repositório central.

- **Porta:** 8082
- **Banco:** PostgreSQL `roomflow_usuarios`, gerenciado com Flyway
- **Stack:** Java 26, Spring Boot 4.1, Spring Data JPA

## Endpoints

Base: `/api/v1/usuarios`

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| GET | `/` | Lista usuários | 200 |
| GET | `/{id}` | Busca usuário por id | 200, 404 |
| POST | `/` | Cadastra usuário (`nome`, `email`) | 201, 400, 409 |
| PUT | `/{id}` | Atualiza nome e e-mail | 200, 400, 404, 409 |
| DELETE | `/{id}` | Anonimiza o usuário | 204, 404 |

## Regras

- O e-mail é único. Cadastrar ou atualizar para um e-mail existente devolve 409.
- Excluir **anonimiza** o usuário: nome e e-mail são substituídos e ele fica inativo. Daí em diante ele responde 404, como se não existisse.
- Contrato usado pelo reservas-ms: `GET /api/v1/usuarios/{id}` devolve **200** para usuário ativo e **404** para inexistente ou inativo.
- Erros seguem o padrão `ProblemDetail` (RFC 9457).

## Como rodar isoladamente

Crie um arquivo `.env` na pasta do serviço (ele não vai para o git) com as credenciais do banco:

```env
DB_USER=roomflow
DB_PASSWORD=troque-esta-senha
```

Depois:

```bash
docker compose up --build
```

O compose sobe a aplicação e o banco (porta 5433 no host). Para rodar pela IDE, defina as variáveis `DB_HOST` (ex.: `localhost:5433`), `DB_USER` e `DB_PASSWORD`.

## Testes

```bash
./mvnw test
```
