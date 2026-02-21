# Índice da Documentação - Projeto SAGA

## 📚 Documentação Completa

### 1. [EXECUTIVE_SUMMARY.md](./EXECUTIVE_SUMMARY.md)
Resumo executivo para apresentação rápida do projeto.

### 2. [README.md](./README.md)
Visão geral do projeto, tecnologias e próximos passos.

### 3. [DESIGN.md](./DESIGN.md)
Documento principal de design e arquitetura:
- Visão geral do problema de negócio
- Arquitetura SAGA Orquestrado
- Componentes principais (Kafka, PostgreSQL, MongoDB, Redis)
- Fluxo de transferência com uso de SQL, NoSQL e cache
- Ferramenta de log recomendada (Grafana Loki ou ELK)
- Tópicos Kafka
- Estados do SAGA
- Circuit Breaker Configuration
- Segurança e observabilidade

### 4. [SAGA_DIAGRAM.md](./SAGA_DIAGRAM.md)
Diagramas detalhados em formato texto:
- Diagrama de sequência - Fluxo principal
- Diagrama de sequência - Fluxo de compensação
- Diagrama de estados do SAGA
- Arquitetura de componentes
- Fluxo de mensagens Kafka
- Circuit Breaker States
- Exemplos de payloads

### 5. [ARCHITECTURE.md](./ARCHITECTURE.md)
Arquitetura técnica detalhada:
- Stack tecnológica (Java/Spring Boot e Node.js/TypeScript)
- Estrutura de microsserviços e referência ao docker-compose
- Detalhamento de cada serviço (PostgreSQL, MongoDB, Redis)
- Configuração do Kafka
- Circuit Breaker Configuration
- Banco de dados SQL (PostgreSQL) e NoSQL (MongoDB), Redis (cache)
- Ferramenta de log (Loki/ELK)
- Segurança, observabilidade, escalabilidade, testes, deployment

### 6. [REQUIREMENTS.md](./REQUIREMENTS.md)
Requisitos funcionais e não funcionais:
- Requisitos funcionais (RF01-RF12)
- Requisitos não funcionais (RNF01-RNF10)
- Casos de uso (CU01-CU05)
- Regras de negócio (RN01-RN06)
- Restrições (RE01-RE04)

### 7. [RUNNING_LOCALLY.md](./RUNNING_LOCALLY.md)
Como rodar a infra localmente e o que é necessário para validar o fluxo SAGA de ponta a ponta.

### 8. Diagramas Mermaid
Diagramas interativos em formato Mermaid (visualizáveis no GitHub, GitLab, etc.):

- [diagrams/saga-flow.mermaid](./diagrams/saga-flow.mermaid)
  - Diagrama de sequência completo do fluxo SAGA
  
- [diagrams/architecture.mermaid](./diagrams/architecture.mermaid)
  - Arquitetura completa do sistema
  
- [diagrams/saga-states.mermaid](./diagrams/saga-states.mermaid)
  - Máquina de estados do SAGA
  
- [diagrams/circuit-breaker.mermaid](./diagrams/circuit-breaker.mermaid)
  - Estados do Circuit Breaker

## 🎯 Conceitos Principais

### SAGA Orquestrado
Padrão para gerenciar transações distribuídas com compensação automática. O orquestrador central coordena todas as etapas.

### Fluxo de Transferência
1. Validação de conta origem
2. Validação de conta destino
3. Validação de compliance
4. Conversão de moeda
5. Débito na conta origem
6. Crédito na conta destino
7. Notificações
8. Auditoria

### Compensação
Em caso de falha, o sistema executa compensações na ordem inversa para reverter todas as operações.

### Circuit Breaker
Proteção contra falhas em cascata, com estados: CLOSED, OPEN, HALF-OPEN.

## 🏗️ Componentes do Sistema

1. **SAGA Orchestrator** - Orquestra o fluxo completo (estado em PostgreSQL)
2. **Account Service** - Gerencia contas (PostgreSQL, cache Redis)
3. **Validation Service** - Validações de compliance (MongoDB para logs)
4. **Currency Service** - Conversão de moedas (Redis cache)
5. **Transaction Service** - Executa transações (PostgreSQL)
6. **Notification Service** - Envia notificações
7. **Audit Service** - Registra auditoria (MongoDB)

## 📊 Tecnologias e Infraestrutura

- **Mensageria**: Apache Kafka (docker-compose)
- **Banco SQL**: PostgreSQL — SAGA, contas, transações
- **Banco NoSQL**: MongoDB — logs e auditoria
- **Cache**: Redis (docker-compose)
- **Circuit Breaker**: Resilience4j (Java) ou opossum (Node.js)
- **Logs**: Grafana Loki (recomendado) ou ELK Stack
- **Observabilidade**: Prometheus, Grafana, Jaeger

## 🚀 Próximos Passos

1. Revisar documentação
2. Escolher stack tecnológica (Java ou Node.js)
3. Configurar ambiente de desenvolvimento
4. Implementar estrutura base do projeto
5. Configurar Kafka e tópicos
6. Implementar SAGA Orchestrator
7. Desenvolver microsserviços
8. Implementar Circuit Breaker
9. Adicionar testes
10. Configurar observabilidade

## 📖 Como Usar Esta Documentação

1. **Comece pelo EXECUTIVE_SUMMARY.md** para visão rápida
2. **Leia README.md** para visão geral
3. **Estude DESIGN.md** para entender a arquitetura
4. **Consulte SAGA_DIAGRAM.md** para visualizar fluxos
5. **Veja ARCHITECTURE.md** para detalhes técnicos
6. **Revise REQUIREMENTS.md** para requisitos completos
7. **Visualize diagramas Mermaid** para entender fluxos

## 🔗 Links Úteis

- [Padrão SAGA](https://microservices.io/patterns/data/saga.html)
- [Apache Kafka](https://kafka.apache.org/)
- [Circuit Breaker Pattern](https://martinfowler.com/bliki/CircuitBreaker.html)
- [Resilience4j](https://resilience4j.readme.io/)
- [Spring State Machine](https://spring.io/projects/spring-statemachine)

## 📝 Notas

- Esta documentação serve como base para implementação
- Diagramas Mermaid podem ser visualizados em editores como VS Code (com extensão Mermaid)
- Todos os diagramas também estão em formato texto no SAGA_DIAGRAM.md
- A documentação pode ser expandida conforme necessário durante a implementação
