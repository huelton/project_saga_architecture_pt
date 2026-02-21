# Sistema de Transferência Bancária Internacional - SAGA Orquestrado

## 📋 Descrição

Sistema financeiro complexo para processamento de transferências bancárias internacionais utilizando o padrão **SAGA Orquestrado** com mensageria **Apache Kafka**, **Circuit Breaker** e arquitetura de microsserviços.

## 🎯 Objetivo

Implementar um sistema robusto que garanta consistência eventual e compensação automática em operações financeiras distribuídas, demonstrando:

- **SAGA Orquestrado**: Coordenação centralizada de transações distribuídas
- **Mensageria Assíncrona**: Apache Kafka para comunicação entre serviços
- **Circuit Breaker**: Proteção contra falhas em cascata
- **Compensação Automática**: Rollback de operações em caso de falha
- **Observabilidade**: Logs, métricas e tracing distribuído

## 📚 Documentação

- **[EXECUTIVE_SUMMARY.md](./EXECUTIVE_SUMMARY.md)**: Resumo executivo para apresentação rápida
- **[INDEX.md](./INDEX.md)**: Índice completo da documentação
- **[DESIGN.md](./DESIGN.md)**: Documento completo de arquitetura e design
- **[SAGA_DIAGRAM.md](./SAGA_DIAGRAM.md)**: Diagramas detalhados do fluxo SAGA
- **[ARCHITECTURE.md](./ARCHITECTURE.md)**: Arquitetura técnica detalhada
- **[REQUIREMENTS.md](./REQUIREMENTS.md)**: Requisitos funcionais e não funcionais
- **[OBSERVABILITY.md](./OBSERVABILITY.md)**: Prometheus, Loki e Grafana — acesso, configuração e exemplos
- **[docs/JMETER.md](./docs/JMETER.md)**: Instalação do JMeter e cenários de teste de carga
- **[diagrams/](./diagrams/)**: Diagramas Mermaid interativos

## 🏗️ Arquitetura

### Componentes Principais

1. **shared/** — Módulos compartilhados ([saga-common](./shared), kafka-common, circuit-breaker) — DTOs e configuração Kafka/Resilience4j
2. **SAGA Orchestrator**: Orquestra todas as etapas da transferência
3. **Account Service**: Gerencia contas bancárias e operações
4. **Validation Service**: Validações de compliance e limites
5. **Currency Service**: Conversão de moedas
6. **Transaction Service**: Executa transações financeiras
7. **Notification Service**: Envia notificações
8. **Audit Service**: Registra auditoria

### Fluxo de Transferência

1. Validação de conta origem
2. Validação de conta destino
3. Validação de compliance (AML, limites)
4. Conversão de moeda
5. Débito na conta origem
6. Crédito na conta destino
7. Notificações
8. Auditoria

### Compensação

Em caso de falha em qualquer etapa, o sistema executa compensações automáticas na ordem inversa.

## 🛠️ Tecnologias

- **Backend**: Java/Spring Boot ou Node.js/TypeScript
- **Mensageria**: Apache Kafka (infra no `docker-compose.yml`)
- **Banco SQL**: PostgreSQL — estado SAGA, contas, transações
- **Banco NoSQL**: MongoDB — logs e auditoria
- **Cache**: Redis — taxas de câmbio, limites (infra no `docker-compose.yml`)
- **Circuit Breaker**: Resilience4j (Java) ou opossum (Node.js)
- **API Gateway**: Spring Cloud Gateway
- **Logs**: Grafana Loki (recomendado) ou ELK Stack
- **Observabilidade**: Prometheus, Grafana, Jaeger

## 📊 Tópicos Kafka

### Comandos
- `transfer.initiate`
- `account.validate.origin`
- `account.validate.destination`
- `compliance.validate`
- `currency.convert`
- `transaction.debit`
- `transaction.credit`
- `notification.send`
- `audit.record`

### Eventos
- `transfer.started`
- `account.validated`
- `compliance.approved`
- `currency.converted`
- `transaction.debited`
- `transaction.credited`
- `transfer.completed`
- `transfer.failed`
- `transfer.compensated`

## 🔄 Estados do SAGA

1. PENDING
2. VALIDATING_ORIGIN
3. VALIDATING_DESTINATION
4. VALIDATING_COMPLIANCE
5. CONVERTING_CURRENCY
6. DEBITING
7. CREDITING
8. NOTIFYING
9. AUDITING
10. COMPLETED
11. COMPENSATING
12. FAILED

## 🔒 Segurança

- Autenticação JWT
- Autorização baseada em roles
- Criptografia de dados sensíveis
- Rate limiting
- Validação de entrada
- Logs de auditoria

## 📈 Observabilidade

- Métricas de performance
- Logs estruturados com correlation ID
- Distributed tracing
- Health checks

## 🐳 Infraestrutura e execução local

O projeto já possui `docker-compose.yml` com:

- **PostgreSQL** (5432) — estado SAGA, contas, transações
- **MongoDB** (27017) — logs e auditoria
- **Redis** (6379) — cache
- **Apache Kafka** (9092 entre containers; **29092** para apps no host) + Kafka UI (8082)
- **pgAdmin** (8080) e **Mongo Express** (8081) para administração

Subir com: `docker-compose up -d`.

**Rodar e validar o fluxo localmente:** subir a infra com `docker-compose up -d` e em seguida cada microsserviço (ordem e exemplos de requisição em **[RUNNING_LOCALLY.md](./RUNNING_LOCALLY.md)**).

### Projetos (microsserviços)

| Projeto | Porta | Descrição |
|---------|-------|-----------|
| [saga-orchestrator](./saga-orchestrator) | 8083 | Orquestrador SAGA, API de transferências |
| [account-service](./account-service) | 8084 | Contas, validação, débito/crédito (PostgreSQL) |
| [validation-service](./validation-service) | 8085 | Compliance (MongoDB) |
| [currency-service](./currency-service) | 8086 | Conversão de moeda (Redis cache) |
| [transaction-service](./transaction-service) | 8087 | Registro de transações, repasse para account (PostgreSQL) |
| [notification-service](./notification-service) | 8088 | Notificações (simulado) |
| [audit-service](./audit-service) | 8089 | Auditoria (MongoDB) |
| **Observabilidade** | | |
| Prometheus | 9090 | Métricas |
| Loki | 3100 | Logs operacionais |
| Grafana | 3000 | Dashboards (admin/admin) |

## 🔨 Build com Maven

O projeto tem **POM raiz** na pasta principal e cada microsserviço é um módulo Maven.

**Build de todos os serviços (na raiz do repositório):**
```bash
# Com Maven instalado
mvn clean install

# Com Maven Wrapper (Windows)
mvnw.cmd clean install

# Com Maven Wrapper (Linux/macOS)
./mvnw clean install
```

**Build ou execução de um único serviço:**
```bash
cd saga-orchestrator
mvn spring-boot:run
```

Para gerar o JAR do Maven Wrapper (se ainda não tiver Maven instalado), execute uma vez na raiz: `mvn -N wrapper:wrapper`.

## 🚀 Próximos Passos

1. Subir infra: `docker-compose up -d`
2. Implementar estrutura base do projeto
3. Configurar tópicos Kafka e consumidores
4. Implementar SAGA Orchestrator (PostgreSQL)
5. Desenvolver microsserviços (PostgreSQL/MongoDB/Redis)
6. Implementar Circuit Breaker
7. Adicionar ferramenta de log (Loki ou ELK)
8. Configurar observabilidade

## 📝 Licença

Este é um projeto educacional/demonstrativo.

