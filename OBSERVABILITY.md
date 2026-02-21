# Observabilidade — Prometheus, Loki e Grafana

Este documento descreve o acesso, a configuração e o uso das ferramentas de observabilidade do projeto SAGA, conforme **ARCHITECTURE.md**.

## Visão geral

| Ferramenta | Porta | Uso |
|------------|-------|-----|
| **Prometheus** | 9090 | Métricas (taxa de sucesso, latência, circuit breaker, throughput Kafka) |
| **Loki** | 3100 | Logs operacionais (JSON, correlation ID, sagaId) |
| **Grafana** | 3000 | Dashboards e consultas (Prometheus + Loki) |

**Regra:** MongoDB é usado para **auditoria e eventos de negócio**; **Loki** (ou ELK) para **logs operacionais** (troubleshooting, correlação entre serviços).

---

## 1. Acesso e subida do ambiente

### 1.1 Subir a stack de observabilidade

```bash
# Na raiz do projeto
docker-compose up -d
```

Isso sobe PostgreSQL, MongoDB, Redis, Kafka, pgAdmin, Mongo Express, Kafka UI, **Prometheus**, **Loki** e **Grafana**.

### 1.2 URLs de acesso

| Serviço | URL | Credenciais (ambiente local) |
|---------|-----|------------------------------|
| **Grafana** | http://localhost:3000 | admin / admin |
| **Prometheus** | http://localhost:9090 | — |
| **Loki** (API) | http://localhost:3100 | — |

No primeiro login o Grafana pode pedir troca de senha do usuário `admin`.

---

## 2. Configuração

### 2.1 Prometheus

- **Arquivo:** `infrastructure/prometheus/prometheus.yml`
- **Função:** Define jobs de scrape para cada microsserviço em `host.docker.internal:8083` … `8089`.
- **Requisito nos serviços:** Expor métricas no endpoint do Spring Boot Actuator:
  - `management.endpoints.web.exposure.include=prometheus,health,info`
  - `management.metrics.export.prometheus.enabled=true`

Exemplo em `application.yml` de um serviço:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus,health,info
  metrics:
    export:
      prometheus:
        enabled: true
```

### 2.2 Loki

- **Arquivo:** `infrastructure/loki/loki-config.yml`
- **Função:** Servidor Loki para ingestão e consulta de logs (armazenamento em volume `loki_data`).
- **Envio de logs:** Os aplicativos podem enviar logs para Loki via:
  - **Promtail** (coletando arquivos de log), ou
  - **Push HTTP** direto: `POST http://localhost:3100/loki/api/v1/push` (formato Loki).

Recomendação: usar **logback** com appender que envia JSON estruturado (incluindo `correlationId` e `sagaId`) para um agente (Promtail) ou para a API de push do Loki.

### 2.3 Grafana

- **Datasources:** Provisionados em `infrastructure/grafana/datasources/datasources.yml`.
- **Prometheus:** URL interna `http://prometheus:9090` (default).
- **Loki:** URL interna `http://loki:3100`.

Não é necessário cadastrar datasources manualmente após o primeiro start.

---

## 3. Métricas (Prometheus) — ARCHITECTURE.md

Métricas esperadas no sistema:

- **Taxa de sucesso de transferências**
- **Latência por etapa** (validação, compliance, conversão, débito, crédito)
- **Taxa de compensação**
- **Circuit breaker states** (Resilience4j)
- **Throughput de mensagens Kafka**

Os serviços Spring Boot expõem métricas padrão (JVM, HTTP, Kafka consumer/producer) em `/actuator/prometheus`. Métricas de negócio (ex.: transferências concluídas, compensações) devem ser implementadas com **Micrometer** nos serviços e aparecerão automaticamente no Prometheus.

---

## 4. Logs (Loki) — ARCHITECTURE.md

- **Formato:** Logs estruturados em **JSON** enviados para Loki.
- **Campos obrigatórios:** **Correlation ID** e **sagaId** (ou `transferId`) em todas as requisições.
- **Níveis:** ERROR, WARN, INFO, DEBUG.

Exemplo de linha de log (conceitual):

```json
{"timestamp":"2024-01-15T10:00:00Z","level":"INFO","service":"saga-orchestrator","correlationId":"abc-123","sagaId":"T-001","message":"Transfer started"}
```

Consultas no Grafana (LogQL) usam o datasource Loki.

---

## 5. Exemplos de uso

### 5.1 Básico — Ver targets no Prometheus

1. Abra http://localhost:9090.
2. Menu **Status → Targets**.
3. Verifique os jobs `saga-orchestrator`, `account-service`, etc. (os que estiverem rodando no host).

### 5.2 Básico — Consulta Prometheus no Grafana

1. Login em http://localhost:3000 (admin / admin).
2. **Explore** (ícone de bússola) → datasource **Prometheus**.
3. Exemplo de query: `jvm_memory_used_bytes{job="saga-orchestrator"}`.
4. Execute e visualize em tempo real.

### 5.3 Básico — Consulta Loki no Grafana

1. **Explore** → datasource **Loki**.
2. Query simples: `{job="saga-orchestrator"}` (após configurar job/labels no Promtail ou no push).
3. Ou por nível: `{job="saga-orchestrator"} |= "level=\"ERROR\""`.

### 5.4 Intermediário — Dashboard de taxa de requisições HTTP

1. **Dashboards → New → Import**.
2. Use um dashboard existente (ex.: **Spring Boot 2.1 Statistics**, ID 10280) ou crie um novo.
3. Adicione painéis com queries Prometheus, por exemplo:
   - `rate(http_server_requests_seconds_count{job="saga-orchestrator"}[5m])`

### 5.5 Avançado — Correlação de logs por sagaId no Loki

1. Se os logs tiverem label ou campo `sagaId` (ou `transferId`), use LogQL:
   - `{job=~"saga-orchestrator|account-service"} | json | sagaId="T-001"`
2. Assim é possível seguir um único fluxo de transferência em todos os serviços.

### 5.6 Avançado — Alertas no Prometheus/Grafana

1. No Prometheus, defina regras em `prometheus.yml` (seção `rule_files`) ou use o Grafana para criar alertas.
2. Exemplo: alertar quando `up{job="saga-orchestrator"} == 0` (serviço fora).
3. Configure um **Contact point** no Grafana (e-mail, Slack, etc.) para notificações.

---

## 6. Resumo de portas (observabilidade)

| Porta | Serviço |
|------|---------|
| 3000 | Grafana |
| 3100 | Loki (API e ingestão) |
| 9090 | Prometheus |

Para mais detalhes de arquitetura, veja **ARCHITECTURE.md** (seções Observabilidade, Banco de Dados e Cache, e Ferramenta de log).
