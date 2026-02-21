# Módulos compartilhados (shared)

Conforme **ARCHITECTURE.md**, esta pasta contém bibliotecas reutilizadas pelos microsserviços do projeto SAGA.

## Módulos

| Módulo | Descrição | Uso |
|--------|-----------|-----|
| **saga-common** | Contratos e DTOs | `SagaEvent`, `TransferRequest`, `TransferResponse` — serialização Kafka e API |
| **kafka-common** | Configuração Kafka | Producer/consumer base (bootstrap, serializers) — opcional por serviço |
| **circuit-breaker** | Resilience4j | Configuração de circuit breaker e retry — importado por serviços que chamam APIs externas |

## Build

Na raiz do repositório:

```bash
mvn clean install
```

Os módulos shared são buildados antes dos serviços (ordem dos módulos no POM raiz).

## Dependência nos serviços

Exemplo no `pom.xml` de um serviço:

```xml
<dependency>
    <groupId>com.saga</groupId>
    <artifactId>saga-common</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

O **saga-orchestrator** já utiliza `saga-common` para DTOs. Os demais serviços podem passar a consumir eventos usando `com.saga.common.dto.SagaEvent` quando desejarem alinhar o contrato.
