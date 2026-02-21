# Rodando e validando o fluxo localmente

## O que a arquitetura permite

**Sim.** A arquitetura foi pensada para que **tudo** (infraestrutura + microsserviços) rode em ambiente local e o fluxo SAGA possa ser validado de ponta a ponta.

---

## Situação atual

### ✅ O que já sobe localmente hoje

A **infraestrutura** está no `docker-compose.yml` e sobe com um comando:

| Componente    | Porta | Acesso / UI              | Uso no fluxo                    |
|---------------|------|--------------------------|----------------------------------|
| **PostgreSQL**| 5432 | pgAdmin http://localhost:8080 | Estado SAGA, contas, transações |
| **MongoDB**   | 27017| Mongo Express http://localhost:8081 | Logs e auditoria              |
| **Redis**     | 6379 | CLI ou cliente Redis     | Cache (taxas, limites)           |
| **Kafka**     | 9092 (entre containers) / **29092** (apps no host) | Kafka UI http://localhost:8082 | Mensageria entre serviços   |

**Comando:**

```bash
docker-compose up -d
```

Credenciais (ex.: PostgreSQL: usuário `postgres`, senha `postgres`, DB `meubanco`; MongoDB: `admin`/`admin123`) estão no `docker-compose.yml`.

Com isso você pode:
- Subir toda a infra local
- Criar tópicos no Kafka (Kafka UI ou scripts)
- Conectar aplicações (quando existirem) em PostgreSQL, MongoDB, Redis e Kafka

### ⏳ O que falta para validar o fluxo completo

Os **microsserviços da aplicação** ainda não existem no repositório (apenas documentação e design). Para validar o fluxo SAGA de ponta a ponta é necessário:

1. **Implementar** (ou gerar PoC) dos serviços:
   - SAGA Orchestrator
   - Account Service
   - Validation Service
   - Currency Service
   - Transaction Service
   - Notification Service
   - Audit Service

2. **Configurar** cada serviço para apontar para os recursos locais:
   - PostgreSQL: `localhost:5432`
   - MongoDB: `localhost:27017`
   - Redis: `localhost:6379`
   - Kafka: `localhost:9092` (ver nota abaixo)

3. **Rodar** os serviços (IDE, `mvn spring-boot:run`, `npm run start`, ou incluí-los no Docker Compose).

Quando isso estiver feito, **sim, dá para subir tudo localmente e validar o fluxo** (happy path e compensação).

---

## Conexão dos serviços com a infra local

Quando os serviços rodarem **na sua máquina** (fora do Docker), use:

| Recurso   | URL / connection string (host) |
|-----------|---------------------------------|
| PostgreSQL| `jdbc:postgresql://localhost:5432/meubanco` (user: `postgres`, password: `postgres`) |
| MongoDB   | `mongodb://admin:admin123@localhost:27017` |
| Redis     | `localhost:6379` (sem senha no compose atual) |
| Kafka     | `localhost:29092` (apps na máquina host; dentro do Docker use `kafka:9092`) |

**Kafka**: O `docker-compose` já expõe dois listeners:
- **Dentro do Docker** (outros containers): `kafka:9092`
- **Na máquina host** (apps rodando fora do Docker): `localhost:29092` — use `bootstrap.servers=localhost:29092` nas aplicações.

---

## Configuração do pgAdmin

1. Acesse o pgAdmin em **http://localhost:8080**.
2. **Login do pgAdmin** (interface web):
   - **Email:** `admin@admin.com`
   - **Password:** `admin123`
3. Depois de logar, **adicione o servidor PostgreSQL**:
   - Clique com o botão direito em **Servers** → **Register** → **Server**.
   - **Aba General:** em *Name* use por exemplo `Postgres Local`.
   - **Aba Connection:**
     - **Host name/address:** use **exatamente** `postgres` (nome do serviço no `docker-compose`).
     - **Port:** `5432`
     - **Maintenance database:** `meubanco`
     - **Username:** `postgres`
     - **Password:** `postgres` (marque *Save password* se quiser)
   - Clique em **Save**.

**Importante (pgAdmin rodando no Docker):** não use `localhost` nem `127.0.0.1` no Host. Dentro do container do pgAdmin, "localhost" é o próprio pgAdmin. O servidor PostgreSQL está em outro container; na rede do Docker ele se chama `postgres`. Por isso o Host deve ser **`postgres`**.

Se o pgAdmin estiver instalado **na sua máquina** (fora do Docker), aí sim use Host `localhost`.

Com isso o pgAdmin conecta ao banco `meubanco` (usuário `postgres`). As tabelas do saga-orchestrator, account-service e transaction-service aparecem em **Servers** → **Postgres Local** → **Databases** → **meubanco** → **Schemas** → **public** → **Tables**.

---

## Ordem recomendada para subir os serviços

1. **Infraestrutura**: `docker-compose up -d` (PostgreSQL, MongoDB, Redis, Kafka). Se as aplicações falharem com *password authentication failed for user "postgres"*, veja a seção **Troubleshooting** abaixo (recriar volume do Postgres ou usar variáveis de ambiente).
2. **Serviços** (em qualquer ordem, ou em terminais separados):
   - `cd account-service && mvn spring-boot:run` (porta 8084)
   - `cd validation-service && mvn spring-boot:run` (porta 8085)
   - `cd currency-service && mvn spring-boot:run` (porta 8086)
   - `cd transaction-service && mvn spring-boot:run` (porta 8087)
   - `cd notification-service && mvn spring-boot:run` (porta 8088)
   - `cd audit-service && mvn spring-boot:run` (porta 8089)
   - `cd saga-orchestrator && mvn spring-boot:run` (porta 8083)

3. **Iniciar uma transferência** (exemplo):
   ```bash
   curl -X POST http://localhost:8083/api/transfers \
     -H "Content-Type: application/json" \
     -d "{\"originAccount\":\"BR123456789\",\"destinationAccount\":\"US987654321\",\"amount\":1000,\"originCurrency\":\"BRL\",\"destinationCurrency\":\"USD\",\"clientId\":\"client-001\"}"
   ```
4. **Consultar status** (use o `sagaId` retornado):
   ```bash
   curl http://localhost:8083/api/transfers/{sagaId}
   ```

## Checklist para validar o fluxo localmente

- [ ] Infra no ar: `docker-compose up -d`
- [ ] Tópicos Kafka criados (automático pelo saga-orchestrator ao subir)
- [ ] Serviços rodando (account, validation, currency, transaction, notification, audit, saga-orchestrator)
- [ ] Teste happy path: `POST /api/transfers` e depois `GET /api/transfers/{sagaId}` até status COMPLETED
- [ ] Teste de compensação: forçar falha (ex.: conta inexistente) e ver estado FAILED + compensação

---

## Troubleshooting

### Erro: "Connection refused" ao conectar no PostgreSQL (pgAdmin no Docker)

Se você ver algo como *connection to server at "127.0.0.1", port 5432 failed: Connection refused* ao cadastrar o servidor no pgAdmin, é porque o **Host** está como `localhost` ou `127.0.0.1`. Com o pgAdmin rodando no Docker, use sempre **Host = `postgres`** (nome do serviço no `docker-compose`). Depois de alterar para `postgres`, salve e tente de novo.

### Erro: "FATAL: password authentication failed for user 'postgres'" (PostgreSQL)

Esse erro acontece quando o **volume do PostgreSQL** foi criado na primeira vez com outras credenciais. As variáveis `POSTGRES_USER` e `POSTGRES_PASSWORD` do `docker-compose` só valem na **primeira** inicialização; se o volume já existir, o banco mantém a senha antiga.

**Solução recomendada — executar o script de reset (na raiz do projeto):**

```powershell
.\reset-postgres.ps1
```

O script faz `docker-compose down -v` (remove todos os volumes, inclusive do Postgres) e `docker-compose up -d`. Depois **aguarde ~15 segundos** e suba as aplicações de novo. O banco passará a aceitar usuário `postgres` e senha `postgres`.

**Se preferir fazer manualmente:**

```powershell
docker-compose down -v
docker-compose up -d
```

Aguarde ~15 segundos e inicie as aplicações (saga-orchestrator, account-service, transaction-service).

**Se você tiver PostgreSQL instalado no Windows** usando a porta 5432, pare o serviço antes (ou use outro host/porta), senão o container do Docker não sobe nessa porta.

**Alternativa (sem recriar o volume):** se o seu Postgres ainda está com usuário/senha antigos (ex.: `admin`/`admin123`), informe pelas variáveis de ambiente ao rodar a aplicação:

- **PowerShell:**  
  `$env:SPRING_DATASOURCE_USERNAME="admin"; $env:SPRING_DATASOURCE_PASSWORD="admin123"; mvn spring-boot:run`
- **CMD:**  
  `set SPRING_DATASOURCE_USERNAME=admin && set SPRING_DATASOURCE_PASSWORD=admin123 && mvn spring-boot:run`

**Credenciais após o reset:** usuário `postgres`, senha `postgres`, database `meubanco`. Para usar outro usuário/senha sem alterar código, defina antes de subir os serviços:

- `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` (Spring Boot já as utiliza).

---

## Resumo

| Pergunta | Resposta |
|----------|----------|
| A arquitetura permite subir tudo local e validar o fluxo? | **Sim.** |
| A infra sobe local hoje? | **Sim** — `docker-compose up -d`. |
| O fluxo SAGA completo já pode ser validado hoje? | **Sim** — os microsserviços foram implementados (saga-orchestrator, account-service, validation-service, currency-service, transaction-service, notification-service, audit-service). |
| Como validar local? | Subir a infra, depois cada serviço (ver ordem acima) e usar `POST /api/transfers` e `GET /api/transfers/{sagaId}`. |
