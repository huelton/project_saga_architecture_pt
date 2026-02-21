# Diagrama do Fluxo SAGA - Transferência Bancária Internacional

## Uso de Bancos de Dados e Cache no Fluxo

- **PostgreSQL (SQL)**: Estado da saga, contas, transações (débito/crédito). Todas as escritas transacionais e consultas de consistência usam PostgreSQL.
- **MongoDB (NoSQL)**: Logs de aplicação, auditoria e eventos de negócio (incluindo compliance). Escrita assíncrona após cada etapa relevante.
- **Redis**: Cache de taxas de câmbio (Currency Service), limites de conta e sessões. Leitura/escrita no fluxo de validação e conversão.
- **Kafka**: Mensageria entre orquestrador e serviços; infra disponível no `docker-compose.yml` do projeto.

## Diagrama de Sequência - Fluxo Principal

```
┌─────────┐    ┌──────────────┐    ┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│ Cliente │    │ Orquestrador │    │   Account   │    │  Validation │    │  Currency   │
│         │    │              │    │   Service   │    │   Service   │    │   Service   │
└────┬────┘    └──────┬───────┘    └──────┬──────┘    └──────┬──────┘    └──────┬──────┘
     │                │                    │                   │                   │
     │ 1. Initiate    │                    │                   │                   │
     │───────────────>│                    │                   │                   │
     │                │                    │                   │                   │
     │                │ 2. Validate Origin │                   │                   │
     │                │───────────────────>│                   │                   │
     │                │                    │                   │                   │
     │                │ 3. Origin Validated│                   │                   │
     │                │<───────────────────│                   │                   │
     │                │                    │                   │                   │
     │                │ 4. Validate Dest   │                   │                   │
     │                │───────────────────>│                   │                   │
     │                │                    │                   │                   │
     │                │ 5. Dest Validated  │                   │                   │
     │                │<───────────────────│                   │                   │
     │                │                    │                   │                   │
     │                │ 6. Validate Compliance                   │                   │
     │                │─────────────────────────────────────────>│                   │
     │                │                    │                   │                   │
     │                │ 7. Compliance OK   │                   │                   │
     │                │<─────────────────────────────────────────│                   │
     │                │                    │                   │                   │
     │                │ 8. Convert Currency│                   │                   │
     │                │───────────────────────────────────────────────────────────>│
     │                │                    │                   │                   │
     │                │ 9. Currency Converted                   │                   │
     │                │<───────────────────────────────────────────────────────────│
     │                │                    │                   │                   │
     │                │ 10. Debit Origin   │                   │                   │
     │                │───────────────────>│                   │                   │
     │                │                    │                   │                   │
     │                │ 11. Debit Confirmed│                   │                   │
     │                │<───────────────────│                   │                   │
     │                │                    │                   │                   │
     │                │ 12. Credit Dest    │                   │                   │
     │                │───────────────────>│                   │                   │
     │                │                    │                   │                   │
     │                │ 13. Credit Confirmed                    │                   │
     │                │<───────────────────│                   │                   │
     │                │                    │                   │                   │
     │                │ 14. Complete       │                   │                   │
     │                │                    │                   │                   │
     │ 15. Success    │                    │                   │                   │
     │<───────────────│                    │                   │                   │
```

## Diagrama de Sequência - Fluxo de Compensação

```
┌──────────────┐    ┌─────────────┐    ┌─────────────┐    ┌──────────────┐
│ Orquestrador │    │  Currency   │    │ Transaction │    │   Account    │
│              │    │   Service   │    │   Service   │    │   Service    │
└──────┬───────┘    └──────┬──────┘    └──────┬──────┘    └──────┬───────┘
       │                   │                   │                   │
       │ 1. Credit Failed  │                   │                   │
       │                   │                   │                   │
       │ 2. Compensate     │                   │                   │
       │    Credit         │                   │                   │
       │──────────────────>│                   │                   │
       │                   │                   │                   │
       │ 3. Compensate     │                   │                   │
       │    Debit          │                   │                   │
       │──────────────────────────────────────>│                   │
       │                   │                   │                   │
       │ 4. Release        │                   │                   │
       │    Currency       │                   │                   │
       │──────────────────>│                   │                   │
       │                   │                   │                   │
       │ 5. Unlock Balance │                   │                   │
       │──────────────────────────────────────────────────────────>│
       │                   │                   │                   │
       │ 6. Transfer       │                   │                   │
       │    Failed         │                   │                   │
       │                   │                   │                   │
```

## Diagrama de Estados do SAGA

```
                    ┌──────────┐
                    │  PENDING │
                    └─────┬────┘
                          │
                          ▼
              ┌───────────────────────┐
              │ VALIDATING_ORIGIN     │
              └───────┬───────────────┘
                      │
                      ▼
          ┌───────────────────────────┐
          │ VALIDATING_DESTINATION    │
          └───────┬───────────────────┘
                  │
                  ▼
      ┌───────────────────────────────┐
      │ VALIDATING_COMPLIANCE         │
      └───────┬───────────────────────┘
              │
              ▼
  ┌───────────────────────────────────┐
  │ CONVERTING_CURRENCY              │
  └───────┬──────────────────────────┘
          │
          ▼
  ┌───────────────────────────────────┐
  │ DEBITING                          │
  └───────┬──────────────────────────┘
          │
          ▼
  ┌───────────────────────────────────┐
  │ CREDITING                         │
  └───────┬──────────────────────────┘
          │
          ▼
  ┌───────────────────────────────────┐
  │ NOTIFYING                         │
  └───────┬──────────────────────────┘
          │
          ▼
  ┌───────────────────────────────────┐
  │ AUDITING                          │
  └───────┬───────────────────────────┘
          │
          ▼
      ┌──────────┐
      │COMPLETED │
      └──────────┘

  [Em caso de falha em qualquer etapa]
          │
          ▼
  ┌───────────────────────────────────┐
  │ COMPENSATING                      │
  └───────┬──────────────────────────┘
          │
          ▼
      ┌────────┐
      │ FAILED │
      └────────┘
```

## Arquitetura de Componentes

```
┌─────────────────────────────────────────────────────────────────┐
│                         API Gateway                              │
│                    (Spring Cloud Gateway)                        │
└────────────────────────────┬────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SAGA Orchestrator                             │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  State Machine                                            │  │
│  │  - Gerencia estados                                       │  │
│  │  - Executa compensações                                   │  │
│  └──────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Circuit Breaker (Resilience4j)                          │  │
│  │  - Proteção contra falhas                                │  │
│  │  - Retry com backoff                                     │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                              │
                              │ Kafka Topics
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│   Account     │    │  Validation   │    │   Currency    │
│   Service     │    │   Service     │    │   Service     │
│               │    │               │    │               │
│ - Validate    │    │ - Compliance  │    │ - Convert     │
│ - Debit       │    │ - Limits      │    │ - Reserve     │
│ - Credit      │    │ - AML         │    │               │
└───────────────┘    └───────────────┘    └───────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│ Transaction   │    │ Notification  │    │    Audit      │
│   Service     │    │   Service     │    │   Service     │
│               │    │               │    │               │
│ - Execute     │    │ - Email       │    │ - Log Events  │
│ - Record      │    │ - SMS         │    │ - Compliance  │
│               │    │ - Push        │    │               │
└───────────────┘    └───────────────┘    └───────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                         Apache Kafka                             │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Topics:                                                  │  │
│  │  - transfer.commands                                     │  │
│  │  - transfer.events                                       │  │
│  │  - transfer.dlq                                          │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    Dados e Infraestrutura                        │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────┐  │
│  │ PostgreSQL (SQL) │  │ MongoDB (NoSQL)  │  │ Redis        │  │
│  │ • Estado SAGA    │  │ • Logs aplicação  │  │ • Cache      │  │
│  │ • Contas         │  │ • Auditoria      │  │ • Taxas      │  │
│  │ • Transações     │  │ • Eventos negócio │  │ • Limites    │  │
│  └──────────────────┘  └──────────────────┘  └──────────────┘  │
│  Logs centralizados: Grafana Loki (recomendado) ou ELK Stack     │
└─────────────────────────────────────────────────────────────────┘
```

## Fluxo de Dados: PostgreSQL, MongoDB e Redis

```
Orquestrador                    Account Service              Currency Service
     │                                 │                            │
     │ 1. Início                       │                            │
     │── Persiste estado ─────────────>│ PostgreSQL (saga_instance)  │
     │   Log evento ──────────────────────────────────────────────>│ MongoDB (audit_logs)
     │                                 │                            │
     │ 2. Validação Conta              │                            │
     │                                 │<── Leitura conta/saldo ────│ PostgreSQL
     │                                 │<── Cache limites (opcional)│ Redis
     │                                 │                            │
     │ 3. Conversão Moeda              │                            │
     │                                 │                            │<── Cache taxa ── Redis
     │                                 │                            │── Grava reserva ── Redis
     │                                 │                            │
     │ 4. Débito/Crédito               │                            │
     │                                 │── Transação/block ─────────>│ PostgreSQL
     │                                 │                            │
     │ 5. Auditoria                    │                            │
     │── Evento auditoria ─────────────────────────────────────────>│ MongoDB (audit_logs)
     │                                 │                            │
  (Logs operacionais: enviar para Grafana Loki ou ELK)
```

## Fluxo de Mensagens Kafka

```
┌──────────────┐
│ Orchestrator │
└──────┬───────┘
       │
       │ Publish: transfer.initiate
       ▼
┌─────────────────────────────────────┐
│     Kafka Topic: transfer.commands   │
└──────┬───────────────────────────────┘
       │
       │ Subscribe
       ▼
┌──────────────┐
│Account Service│
└──────┬───────┘
       │
       │ Publish: account.validated
       ▼
┌─────────────────────────────────────┐
│     Kafka Topic: transfer.events     │
└──────┬───────────────────────────────┘
       │
       │ Subscribe
       ▼
┌──────────────┐
│ Orchestrator │
└──────────────┘

[Se falha após retries]
       │
       │ Publish: transfer.failed
       ▼
┌─────────────────────────────────────┐
│     Kafka Topic: transfer.dlq        │
│     (Dead Letter Queue)              │
└─────────────────────────────────────┘
```

## Circuit Breaker States

```
                    ┌──────────────┐
                    │     CLOSED   │
                    │  (Normal)    │
                    └──────┬───────┘
                           │
                    [Falhas > Threshold]
                           │
                           ▼
                    ┌──────────────┐
                    │     OPEN     │
                    │  (Blocking)  │
                    └──────┬───────┘
                           │
                    [Timeout]
                           │
                           ▼
                    ┌──────────────┐
                    │  HALF-OPEN   │
                    │  (Testing)   │
                    └──────┬───────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   [Success]          [Failure]          [Timeout]
        │                  │                  │
        ▼                  ▼                  ▼
   ┌────────┐         ┌────────┐         ┌────────┐
   │ CLOSED │         │  OPEN  │         │  OPEN  │
   └────────┘         └────────┘         └────────┘
```

## Exemplo de Payload de Mensagem

### Comando: Initiate Transfer
```json
{
  "sagaId": "saga-12345",
  "command": "transfer.initiate",
  "timestamp": "2024-01-15T10:30:00Z",
  "data": {
    "originAccount": "BR123456789",
    "destinationAccount": "US987654321",
    "amount": 1000.00,
    "originCurrency": "BRL",
    "destinationCurrency": "USD",
    "clientId": "client-001"
  }
}
```

### Evento: Transfer Completed
```json
{
  "sagaId": "saga-12345",
  "event": "transfer.completed",
  "timestamp": "2024-01-15T10:35:00Z",
  "data": {
    "transferId": "transfer-12345",
    "convertedAmount": 200.00,
    "exchangeRate": 5.0,
    "status": "COMPLETED"
  }
}
```

### Compensação: Compensate Debit
```json
{
  "sagaId": "saga-12345",
  "command": "transaction.compensate.debit",
  "timestamp": "2024-01-15T10:33:00Z",
  "data": {
    "accountId": "BR123456789",
    "amount": 1000.00,
    "reason": "Credit failed",
    "originalTransactionId": "txn-001"
  }
}
```

