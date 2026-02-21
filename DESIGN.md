# Design e Arquitetura - Sistema de Transferência Bancária Internacional

## Visão Geral

Este projeto implementa um sistema de **Transferência Bancária Internacional** utilizando o padrão **SAGA Orquestrado** com mensageria **Apache Kafka**, **Circuit Breaker** e múltiplos microsserviços.

## Problema de Negócio

Uma transferência bancária internacional envolve múltiplas operações que devem ser executadas de forma transacional:
- Validação de conta origem
- Validação de conta destino
- Verificação de limites e compliance
- Conversão de moeda
- Débito na conta origem
- Crédito na conta destino
- Notificações
- Auditoria

Se qualquer etapa falhar, todas as operações anteriores devem ser compensadas (rollback).

## Arquitetura SAGA Orquestrado

### Componentes Principais

1. **Orquestrador (SAGA Orchestrator)**
   - Coordena todas as etapas da transferência
   - Gerencia o estado da transação
   - Executa compensações em caso de falha
   - Implementa Circuit Breaker para resiliência

2. **Microsserviços (Participantes)**
   - **Account Service**: Gerencia contas bancárias
   - **Validation Service**: Validações de compliance e limites
   - **Currency Service**: Conversão de moedas
   - **Transaction Service**: Executa débitos e créditos
   - **Notification Service**: Envia notificações
   - **Audit Service**: Registra auditoria

3. **Mensageria (Kafka)**
   - Tópicos para comandos e eventos
   - Garantia de entrega e ordem
   - Dead Letter Queue para mensagens com falha
   - *Infraestrutura Kafka disponível via `docker-compose.yml` do projeto*

4. **Banco de Dados**
   - **PostgreSQL (SQL)**: Estado do SAGA, contas, transações e dados transacionais
   - **MongoDB (NoSQL)**: Logs de aplicação, auditoria e eventos para análise

5. **Cache (Redis)**
   - Taxas de câmbio (TTL configurável)
   - Limites de conta e sessões
   - Redução de carga nos serviços

6. **Circuit Breaker**
   - Proteção contra falhas em cascata
   - Fallback strategies
   - Timeout e retry policies

## Fluxo de Transferência Internacional

### Fluxo Principal (Happy Path)

1. **Início da Transferência**
   - Cliente solicita transferência internacional
   - Orquestrador recebe comando e cria saga instance
   - **PostgreSQL**: Persiste estado da saga (`saga_instance`, `saga_step`)
   - **MongoDB**: Registra log de início (audit/application log)

2. **Validação de Conta Origem**
   - **PostgreSQL**: Consulta conta e saldo (dados transacionais)
   - **Redis**: Consulta cache de limites diários/mensais (se existir)
   - Verifica se conta existe e está ativa
   - Verifica saldo disponível e limites

3. **Validação de Conta Destino**
   - Verifica se conta destino existe
   - Valida dados bancários (SWIFT, IBAN)
   - Verifica se banco destino está operacional

4. **Validação de Compliance**
   - Verifica limites regulatórios
   - Anti-lavagem de dinheiro (AML)
   - Sanções internacionais

5. **Conversão de Moeda**
   - **Redis**: Consulta cache de taxa de câmbio (TTL ex.: 5 min)
   - Se não houver cache: consulta API externa e grava no Redis
   - Calcula valor convertido
   - Reserva taxa por tempo limitado (Redis)

6. **Débito na Conta Origem**
   - **PostgreSQL**: Bloqueia valor na conta origem e registra transação pendente
   - Dados transacionais permanecem em SQL para consistência

7. **Crédito na Conta Destino**
   - Credita valor convertido na conta destino
   - Confirma transação

8. **Confirmação de Débito**
   - Confirma débito na conta origem
   - Libera bloqueio

9. **Notificações**
   - Notifica cliente origem
   - Notifica cliente destino
   - Notifica sistemas internos

10. **Auditoria**
    - **MongoDB**: Registra todas as etapas em collections de logs/auditoria
    - Gera logs de compliance e eventos da aplicação
    - Armazena para auditoria regulatória (retenção conforme política)

### Fluxo de Compensação (Rollback)

Se qualquer etapa falhar, o orquestrador executa compensações na ordem inversa:

1. **Falha em Crédito**: Reverte débito na conta origem
2. **Falha em Débito**: Libera bloqueio de saldo
3. **Falha em Conversão**: Cancela reserva de taxa
4. **Falha em Compliance**: Libera validações
5. **Falha em Validação**: Notifica erro ao cliente

## Tecnologias

- **Linguagem**: Java/Spring Boot ou Node.js/TypeScript
- **Mensageria**: Apache Kafka (infra disponível no `docker-compose.yml`)
- **Circuit Breaker**: Resilience4j (Java) ou opossum (Node.js)
- **Banco de Dados SQL**: PostgreSQL — estado do SAGA, contas, transações
- **Banco de Dados NoSQL**: MongoDB — logs de aplicação e auditoria
- **Cache**: Redis — taxas de câmbio, limites, sessões
- **API Gateway**: Spring Cloud Gateway ou Kong
- **Service Discovery**: Consul ou Eureka
- **Observabilidade**: Prometheus + Grafana; **Logs**: ferramenta recomendada abaixo

## Padrões de Design

1. **SAGA Orquestrado**: Orquestrador centralizado gerencia o fluxo
2. **Event Sourcing**: Eventos para rastreabilidade
3. **CQRS**: Separação de leitura e escrita
4. **Circuit Breaker**: Proteção contra falhas
5. **Retry Pattern**: Tentativas automáticas
6. **Dead Letter Queue**: Mensagens com falha
7. **Saga State Machine**: Máquina de estados para o SAGA

## Tópicos Kafka

### Comandos (Commands)
- `transfer.initiate` - Inicia transferência
- `account.validate.origin` - Valida conta origem
- `account.validate.destination` - Valida conta destino
- `compliance.validate` - Valida compliance
- `currency.convert` - Converte moeda
- `transaction.debit` - Débito
- `transaction.credit` - Crédito
- `notification.send` - Envia notificação
- `audit.record` - Registra auditoria

### Eventos (Events)
- `transfer.started` - Transferência iniciada
- `account.validated` - Conta validada
- `compliance.approved` - Compliance aprovado
- `currency.converted` - Moeda convertida
- `transaction.debited` - Débito realizado
- `transaction.credited` - Crédito realizado
- `transfer.completed` - Transferência completa
- `transfer.failed` - Transferência falhou
- `transfer.compensated` - Compensação executada

### Dead Letter Queue
- `transfer.dlq` - Mensagens com falha após retries

## Estados do SAGA

1. **PENDING** - Aguardando início
2. **VALIDATING_ORIGIN** - Validando conta origem
3. **VALIDATING_DESTINATION** - Validando conta destino
4. **VALIDATING_COMPLIANCE** - Validando compliance
5. **CONVERTING_CURRENCY** - Convertendo moeda
6. **DEBITING** - Executando débito
7. **CREDITING** - Executando crédito
8. **NOTIFYING** - Enviando notificações
9. **AUDITING** - Registrando auditoria
10. **COMPLETED** - Transferência completa
11. **COMPENSATING** - Executando compensação
12. **FAILED** - Transferência falhou

## Circuit Breaker Configuration

- **Failure Threshold**: 5 falhas consecutivas
- **Timeout**: 30 segundos por chamada
- **Half-Open Interval**: 60 segundos
- **Retry**: 3 tentativas com backoff exponencial
- **Fallback**: Retorna erro estruturado ou usa cache

## Segurança

- Autenticação JWT
- Autorização baseada em roles
- Criptografia de dados sensíveis
- Rate limiting
- Validação de entrada
- Logs de auditoria

## Observabilidade

- **Métricas**: Taxa de sucesso, latência, throughput (Prometheus + Grafana)
- **Logs**: Estruturados (JSON) com correlation ID; ver seção *Ferramenta de Log* abaixo
- **Tracing**: Distributed tracing (Jaeger/Zipkin)
- **Health Checks**: Endpoints de saúde para cada serviço

### Ferramenta de Log (recomendação)

Recomenda-se centralizar logs em uma das seguintes opções:

1. **Grafana Loki** (recomendado para este projeto)
   - Leve, integra nativamente com Grafana (já usada para métricas)
   - Logs estruturados (JSON), correlation ID, labels por serviço/sagaId
   - Consultas LogQL, alertas e dashboards junto com métricas
   - Pode ser adicionado ao `docker-compose.yml` para ambiente local

2. **ELK Stack** (Elasticsearch, Logstash, Kibana)
   - Opção robusta para busca full-text e análise avançada
   - Indicada se houver necessidade de retenção longa e compliance pesado em logs
   - Maior consumo de recursos que Loki

Os serviços devem enviar logs estruturados (JSON) para a ferramenta escolhida; MongoDB continua sendo o repositório de **auditoria e eventos de negócio**, enquanto a ferramenta de log é para **operacional e troubleshooting**.

## Escalabilidade

- Horizontal scaling de microsserviços
- Particionamento de tópicos Kafka
- **Cache distribuído (Redis)**: reduz acessos a PostgreSQL e a APIs externas
- Load balancing

## Considerações de Compliance

- LGPD/GDPR: Proteção de dados pessoais
- PCI-DSS: Segurança de dados de cartão
- Regulamentações bancárias
- Retenção de logs por período legal
- Relatórios de auditoria

