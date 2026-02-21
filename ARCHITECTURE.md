# Arquitetura Técnica Detalhada

## Visão Geral da Arquitetura

Este documento detalha a arquitetura técnica do sistema de Transferência Bancária Internacional usando SAGA Orquestrado.

## Stack Tecnológica Recomendada

### Opção 1: Java/Spring Boot (Recomendado)
- **Framework**: Spring Boot 3.x
- **SAGA**: Custom implementation ou Axon Framework
- **Kafka**: Spring Kafka
- **Circuit Breaker**: Resilience4j
- **Database**: Spring Data JPA (PostgreSQL), Spring Data MongoDB
- **API**: Spring WebFlux (Reactive)
- **Service Discovery**: Spring Cloud Consul
- **API Gateway**: Spring Cloud Gateway

### Opção 2: Node.js/TypeScript
- **Framework**: NestJS
- **SAGA**: Custom implementation
- **Kafka**: kafkajs
- **Circuit Breaker**: opossum
- **Database**: TypeORM (PostgreSQL), Mongoose (MongoDB)
- **API**: Express/Fastify
- **Service Discovery**: Consul
- **API Gateway**: Kong

## Estrutura de Microsserviços

```
projeto_saga/
├── saga-orchestrator/          # Orquestrador central
├── account-service/            # Serviço de contas
├── validation-service/         # Serviço de validações
├── currency-service/           # Serviço de conversão
├── transaction-service/         # Serviço de transações
├── notification-service/        # Serviço de notificações
├── audit-service/              # Serviço de auditoria
├── api-gateway/                # Gateway de API
├── shared/                     # Bibliotecas compartilhadas
│   ├── saga-common/            # Contratos e DTOs
│   ├── kafka-common/           # Configuração Kafka
│   └── circuit-breaker/        # Circuit breaker config
└── infrastructure/             # Infraestrutura
    ├── docker-compose.yml      # Kafka, PostgreSQL, MongoDB, Redis (já configurado)
    └── kafka-topics/           # Scripts de criação de tópicos
```

**Infraestrutura**: O projeto já possui `docker-compose.yml` com PostgreSQL, MongoDB, Kafka (e Kafka UI), pgAdmin, Mongo Express e Redis. Subir com `docker-compose up -d` para desenvolvimento local.

## Detalhamento dos Serviços

### 1. SAGA Orchestrator

**Responsabilidades:**
- Gerenciar estado da saga
- Orquestrar sequência de comandos
- Executar compensações
- Implementar Circuit Breaker
- Persistir estado da saga

**Tecnologias:**
- State Machine (Spring State Machine ou custom)
- Kafka Producer/Consumer (Kafka disponível no docker-compose)
- PostgreSQL (SQL) para estado da saga
- Resilience4j para Circuit Breaker

**Endpoints:**
- `POST /api/transfers` - Iniciar transferência
- `GET /api/transfers/{sagaId}` - Consultar status
- `POST /api/transfers/{sagaId}/compensate` - Compensação manual

### 2. Account Service

**Responsabilidades:**
- Validar contas (origem e destino)
- Verificar saldo
- Executar débitos e créditos
- Gerenciar bloqueios de saldo

**Tecnologias:**
- Spring Data JPA
- PostgreSQL (dados transacionais: contas, saldos)
- Redis (cache opcional de limites)
- Kafka Consumer/Producer

**Endpoints:**
- `POST /api/accounts/validate` - Validar conta
- `POST /api/accounts/debit` - Débito
- `POST /api/accounts/credit` - Crédito
- `POST /api/accounts/compensate` - Compensação

**Kafka Topics:**
- Consome: `account.validate.origin`, `account.validate.destination`
- Produz: `account.validated`, `account.debited`, `account.credited`

### 3. Validation Service

**Responsabilidades:**
- Validação de compliance
- Verificação de limites (diário, mensal)
- Anti-lavagem de dinheiro (AML)
- Verificação de sanções

**Tecnologias:**
- Spring Boot
- MongoDB (NoSQL) para logs de compliance e eventos
- Integração com APIs externas (sanctions list)

**Endpoints:**
- `POST /api/validation/compliance` - Validar compliance
- `GET /api/validation/limits/{accountId}` - Consultar limites

**Kafka Topics:**
- Consome: `compliance.validate`
- Produz: `compliance.approved`, `compliance.rejected`

### 4. Currency Service

**Responsabilidades:**
- Consultar taxas de câmbio
- Converter valores
- Reservar taxa por tempo limitado
- Cancelar reserva em caso de compensação

**Tecnologias:**
- Spring Boot
- Integração com API de câmbio (ex: ExchangeRate API)
- Redis para cache de taxas de câmbio (TTL ex.: 5 min) e reservas
- Circuit Breaker para API externa

**Endpoints:**
- `POST /api/currency/convert` - Converter moeda
- `POST /api/currency/reserve` - Reservar taxa
- `POST /api/currency/cancel-reservation` - Cancelar reserva

**Kafka Topics:**
- Consome: `currency.convert`
- Produz: `currency.converted`, `currency.reservation-cancelled`

### 5. Transaction Service

**Responsabilidades:**
- Registrar transações
- Executar débitos e créditos (coordenação)
- Manter histórico de transações

**Tecnologias:**
- Spring Data JPA
- PostgreSQL
- Event Sourcing (opcional)

**Endpoints:**
- `POST /api/transactions/execute` - Executar transação
- `GET /api/transactions/{id}` - Consultar transação

### 6. Notification Service

**Responsabilidades:**
- Enviar emails
- Enviar SMS
- Push notifications
- Retry em caso de falha

**Tecnologias:**
- Spring Boot
- Integração com serviços de email/SMS
- Circuit Breaker
- Dead Letter Queue

**Endpoints:**
- `POST /api/notifications/send` - Enviar notificação

**Kafka Topics:**
- Consome: `notification.send`
- Produz: `notification.sent`, `notification.failed`

### 7. Audit Service

**Responsabilidades:**
- Registrar todos os eventos
- Armazenar logs de compliance
- Gerar relatórios de auditoria
- Retenção de dados conforme regulamentação

**Tecnologias:**
- Spring Boot
- MongoDB (NoSQL) para logs de auditoria e eventos de negócio
- Kafka Consumer
- Logs operacionais enviados para ferramenta central (Loki ou ELK)

**Endpoints:**
- `POST /api/audit/record` - Registrar evento
- `GET /api/audit/reports` - Gerar relatórios

**Kafka Topics:**
- Consome: `audit.record`
- Produz: `audit.recorded`

## Configuração do Kafka

### Tópicos Necessários

```bash
# Comandos
transfer.commands (partitions: 6, replication: 3)
account.commands (partitions: 3, replication: 3)
compliance.commands (partitions: 3, replication: 3)
currency.commands (partitions: 3, replication: 3)
transaction.commands (partitions: 3, replication: 3)
notification.commands (partitions: 3, replication: 3)

# Eventos
transfer.events (partitions: 6, replication: 3)
account.events (partitions: 3, replication: 3)
compliance.events (partitions: 3, replication: 3)
currency.events (partitions: 3, replication: 3)
transaction.events (partitions: 3, replication: 3)
notification.events (partitions: 3, replication: 3)

# Dead Letter Queue
transfer.dlq (partitions: 3, replication: 3)
```

### Configuração de Consumer Groups

- `saga-orchestrator-group` - Orquestrador
- `account-service-group` - Account Service
- `validation-service-group` - Validation Service
- `currency-service-group` - Currency Service
- `transaction-service-group` - Transaction Service
- `notification-service-group` - Notification Service
- `audit-service-group` - Audit Service

## Circuit Breaker Configuration

### Resilience4j (Java)

```yaml
resilience4j:
  circuitbreaker:
    instances:
      accountService:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 60s
        failureRateThreshold: 50
        eventConsumerBufferSize: 10
      currencyService:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        waitDurationInOpenState: 30s
        failureRateThreshold: 50
  retry:
    instances:
      accountService:
        maxAttempts: 3
        waitDuration: 1s
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
      currencyService:
        maxAttempts: 3
        waitDuration: 500ms
  timelimiter:
    instances:
      accountService:
        timeoutDuration: 30s
      currencyService:
        timeoutDuration: 10s
```

## Banco de Dados e Cache

### PostgreSQL (SQL) — Dados transacionais e estado do SAGA

Uso: estado da saga, contas, saldos, transações. Todas as operações que exigem consistência forte e transações ACID utilizam PostgreSQL. Disponível no `docker-compose.yml` (porta 5432, db `meubanco`).

**Schema SAGA:**
```sql
CREATE TABLE saga_instance (
    id VARCHAR(255) PRIMARY KEY,
    saga_type VARCHAR(100) NOT NULL,
    current_state VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    payload JSONB,
    compensation_data JSONB
);

CREATE TABLE saga_step (
    id VARCHAR(255) PRIMARY KEY,
    saga_id VARCHAR(255) REFERENCES saga_instance(id),
    step_name VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    executed_at TIMESTAMP,
    compensated_at TIMESTAMP,
    error_message TEXT
);
```

### MongoDB (NoSQL) — Logs e auditoria

Uso: logs de aplicação, auditoria regulatória, eventos de negócio e compliance. Escritas assíncronas; ideal para volume alto de eventos e consultas por período. Disponível no `docker-compose.yml` (porta 27017).

**Collections:**
- `audit_logs` - Logs de auditoria
- `compliance_logs` - Logs de compliance
- `transfer_events` - Eventos de transferência
- `application_logs` - Eventos da aplicação (opcional; pode ser só na ferramenta de log)

### Redis — Cache

Disponível no `docker-compose.yml` (porta 6379). Uso no fluxo:

- **Currency Service**: taxas de câmbio (TTL: 5 minutos), reserva de taxa
- **Account/Validation**: limites de conta (TTL: 1 hora) para reduzir carga no PostgreSQL
- Estado de sessão quando necessário

### Ferramenta de log (recomendação)

Para logs operacionais (troubleshooting, métricas de log, correlation ID entre serviços), recomenda-se centralizar em:

1. **Grafana Loki** (recomendado): leve, integração com Grafana, LogQL. Pode ser adicionado ao `docker-compose.yml` para ambiente local.
2. **ELK Stack** (Elasticsearch, Logstash, Kibana): para cenários com busca full-text e retenção longa de logs operacionais.

MongoDB permanece como repositório de **auditoria e eventos de negócio**; a ferramenta de log é para **logs operacionais**.

## Segurança

### Autenticação e Autorização

- **JWT Tokens**: Autenticação stateless
- **OAuth2**: Para integração com sistemas externos
- **Role-Based Access Control (RBAC)**: Controle de acesso

### Criptografia

- **Dados em trânsito**: TLS 1.3
- **Dados em repouso**: AES-256
- **Sensíveis**: Campos específicos criptografados

### Rate Limiting

- API Gateway: 100 req/min por cliente
- Serviços internos: 1000 req/min

## Observabilidade

### Métricas (Prometheus)

- Taxa de sucesso de transferências
- Latência por etapa
- Taxa de compensação
- Circuit breaker states
- Throughput de mensagens Kafka

### Logs (Grafana Loki recomendado ou ELK Stack)

- Logs estruturados em JSON enviados para Loki ou ELK
- Correlation ID (e sagaId) em todas as requisições
- Níveis: ERROR, WARN, INFO, DEBUG
- MongoDB usado para auditoria/eventos de negócio; Loki/ELK para operacional

### Tracing (Jaeger/Zipkin)

- Distributed tracing
- Span por cada etapa do SAGA
- Tempo de resposta por serviço

## Escalabilidade

### Horizontal Scaling

- Cada serviço pode escalar independentemente
- Kafka particionamento permite paralelismo
- Load balancer distribui carga

### Performance

- Cache agressivo (Redis)
- Connection pooling
- Async processing onde possível
- Batch processing para auditoria

## Testes

### Estratégia de Testes

1. **Unit Tests**: Lógica de negócio
2. **Integration Tests**: Integração com Kafka e DB
3. **Contract Tests**: Contratos entre serviços
4. **End-to-End Tests**: Fluxo completo
5. **Chaos Tests**: Testes de resiliência

### Cenários de Teste

- Happy path completo
- Falha em cada etapa (teste de compensação)
- Timeout de serviços
- Circuit breaker activation
- Kafka downtime
- Database failure

## Deployment

### Containerização

- Docker para cada serviço
- Docker Compose para desenvolvimento
- Kubernetes para produção

### CI/CD

- GitHub Actions / GitLab CI
- Build automático
- Testes automáticos
- Deploy em ambientes (dev, staging, prod)

## Monitoramento e Alertas

### Alertas Críticos

- Taxa de falha > 5%
- Latência > 30s
- Circuit breaker aberto > 5min
- Dead letter queue com mensagens
- Database connection errors

### Dashboards

- Grafana: Métricas em tempo real
- Kibana: Análise de logs
- Jaeger UI: Tracing de requisições

