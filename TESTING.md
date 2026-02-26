# Testes — Projeto SAGA

## Visão geral

Cada microsserviço possui testes unitários e de integração com JUnit 5 e Spring Boot Test. A cobertura de código é exigida em **90%** (linhas), verificada pelo **JaCoCo** no `mvn verify`.

## Cobertura (JaCoCo 90%)

- **Meta:** 90% de cobertura de linhas por módulo.
- **Comando:** `mvn verify` (roda testes e gera relatório em `target/site/jacoco/index.html`).
- **Exclusões:** `Application`, pacotes `config`, `dto` (e onde aplicável `document`, `entity`) não entram no cálculo para manter o foco em regras de negócio e integrações.

## Convenção: constantes nos testes

Valores literais em testes devem ser centralizados em constantes para organização e manutenção:

- Cada módulo possui uma classe **`TestConstants`** em `src/test/.../constants/` com IDs, valores e paths usados nos testes.
- Use as constantes de produção (ex.: `SagaConstants.STATE_PENDING`, `AccountConstants.TOPIC_*`) quando o valor for o mesmo do código de produção.
- Exemplos: `TestConstants.TRANSFER_ID_1`, `TestConstants.ACCOUNT_ID_TEST`, `TestConstants.API_TRANSFERS`.

## Executando os testes

### Por microsserviço (Maven)

Na raiz de cada serviço:

```bash
cd saga-orchestrator && mvn test
cd account-service && mvn test
cd validation-service && mvn test
cd currency-service && mvn test
cd transaction-service && mvn test
cd notification-service && mvn test
cd audit-service && mvn test
```

### Perfil de teste

Os testes usam o perfil `test` e arquivos `application-test.yml` em cada serviço, com:

- **JPA (PostgreSQL):** H2 em memória para saga-orchestrator, account-service e transaction-service.
- **MongoDB:** URI de teste para validation-service e audit-service.
- **Redis:** Configuração local para currency-service (pode ser mockada).
- **Kafka:** Bootstrap servers local (testes de contexto podem usar mocks).

## Estrutura de testes

- **Constants:** testes dos valores de tópicos e constantes (KafkaConstants, SagaConstants, etc.).
- **Kafka:** testes de carga de contexto dos consumers e producers (com Kafka mockado quando necessário).
- **Repository:** testes JPA/Mongo com repositórios (DataJpaTest / embed MongoDB quando aplicável).
- **Service:** testes dos serviços com dependências Kafka/outros mockadas.
- **Controller:** testes de API com MockMvc e serviços mockados (ex.: TransferControllerTest).

## Observações

- Testes que dependem de Kafka sem embedded broker podem falhar se o Kafka não estiver disponível; use mocks ou `@EmbeddedKafka` onde for necessário.
- Para rodar todos os serviços e testes de integração de ponta a ponta, suba a infra com `docker-compose up -d` e execute cada serviço e seus testes na ordem documentada em RUNNING_LOCALLY.md.
