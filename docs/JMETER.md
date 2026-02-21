# Testes de carga e cenários com Apache JMeter

Este documento descreve a instalação do JMeter passo a passo e os cenários de teste para validação do sistema SAGA (transferências, compensação, resiliência).

---

## 1. Instalação do JMeter (passo a passo)

### 1.1 Pré-requisitos

- **Java (JDK) 11 ou superior** instalado e `JAVA_HOME` configurado.
- Verificar: `java -version` e `echo %JAVA_HOME%` (Windows) ou `echo $JAVA_HOME` (Linux/macOS).

### 1.2 Download

1. Acesse: https://jmeter.apache.org/download_jmeter.cgi
2. Baixe o **Binary** (arquivo `.tgz` ou `.zip`), por exemplo: `apache-jmeter-5.6.3.zip`
3. Extraia para uma pasta, por exemplo: `C:\Tools\apache-jmeter-5.6.3` (Windows) ou `/opt/apache-jmeter-5.6.3` (Linux/macOS)

### 1.3 Configuração do ambiente (opcional)

- **Windows:** Adicione a pasta `bin` ao PATH, por exemplo:  
  `C:\Tools\apache-jmeter-5.6.3\bin`
- **Linux/macOS:**  
  `export PATH=$PATH:/opt/apache-jmeter-5.6.3/bin`

### 1.4 Executar o JMeter

- **Interface gráfica (GUI):**
  - Windows: `jmeter.bat` (dentro da pasta `bin`)
  - Linux/macOS: `./jmeter` (dentro da pasta `bin`)
- **Modo linha de comando (para rodar plano de teste):**
  - `jmeter -n -t caminho/do/plano.jmx -l resultado.csv -e -o relatorio/`

### 1.5 Verificação

Ao abrir o JMeter, deve aparecer a janela principal com a árvore “Test Plan”. A instalação está correta.

---

## 2. Cenários de teste para validação

Conforme **ARCHITECTURE.md** e **REQUIREMENTS.md**, os cenários abaixo validam o fluxo SAGA e a resiliência.

### 2.1 Cenário 1 — Happy path (fluxo completo)

**Objetivo:** Validar uma transferência que percorre todas as etapas com sucesso.

**Passos:**

1. **POST /api/transfers** (saga-orchestrator, porta 8083) com body JSON:
   - `originAccountId`, `destinationAccountId`, `amount`, `currency`
2. Obter o identificador da transferência da resposta (ex.: `transferId` ou `id`).
3. **GET /api/transfers/{transferId}/status** em loop (polling) até `status` = `COMPLETED` ou timeout.

**Critério de sucesso:** Resposta 202 no POST e, em seguida, status `COMPLETED` no GET dentro do tempo esperado.

### 2.2 Cenário 2 — Múltiplas transferências (carga)

**Objetivo:** Medir throughput e latência sob carga.

**Passos:**

1. Thread Group com N threads (ex.: 10) e R ramp-up (ex.: 10 s).
2. Loop: POST /api/transfers com dados válidos (contas existentes, ex.: ACC-001, ACC-002).
3. Opcional: GET status após cada POST para confirmar conclusão.

**Métricas a observar:** Requisições por segundo, tempo de resposta (média, p95, p99), taxa de erro.

### 2.3 Cenário 3 — Falha em uma etapa (compensação)

**Objetivo:** Validar que uma falha em uma etapa (ex.: validação ou débito) resulta em compensação e estado FAILED/COMPENSATING.

**Passos:**

1. Configurar um serviço para falhar (ex.: mock ou desligar temporariamente um microsserviço).
2. Enviar POST /api/transfers.
3. Consultar GET /api/transfers/{transferId}/status até estado final (FAILED ou COMPENSATING).

**Critério de sucesso:** O orquestrador não deixa a saga em estado inconsistente; estado final reflete falha/compensação.

### 2.4 Cenário 4 — Timeout e resiliência

**Objetivo:** Validar comportamento com serviço lento ou indisponível.

**Passos:**

1. Aumentar delay em um serviço (ex.: validation ou currency) ou desligar um serviço.
2. Enviar POST /api/transfers.
3. Verificar timeouts configurados e se o circuito de compensação ou circuit breaker é acionado conforme ARCHITECTURE.md.

### 2.5 Cenário 5 — Consulta de status (GET)

**Objetivo:** Medir desempenho do endpoint de status.

**Passos:**

1. Thread Group com muitas threads (ex.: 50).
2. GET /api/transfers/{transferId}/status com um ou vários `transferId` válidos.
3. Coletar latência e taxa de erro.

---

## 3. Plano de teste JMeter (exemplo)

O projeto pode incluir um arquivo JMX de exemplo em `docs/jmeter/` (ex.: `saga-transfer-basic.jmx`) com:

- **Thread Group:** 5 usuários, 1 iteração cada (ou 10 usuários, 5 iterações).
- **HTTP Request (POST):**  
  - Server: localhost, Port: 8083, Path: `/api/transfers`  
  - Method: POST, Body: JSON com `originAccountId`, `destinationAccountId`, `amount`, `currency`
- **View Results Tree** e **Summary Report** (ou **Backend Listener** para Grafana/Prometheus, se desejado).

Para rodar em modo não-GUI:

```bash
jmeter -n -t docs/jmeter/saga-transfer-basic.jmx -l resultado.jtl -e -o relatorio/
```

O relatório HTML será gerado na pasta `relatorio/`.

---

## 4. Checklist de validação

- [ ] JMeter instalado e abrindo corretamente.
- [ ] Cenário 1 (happy path) executado com sucesso (202 + COMPLETED).
- [ ] Cenário 2 (carga) executado; métricas anotadas (throughput, latência).
- [ ] Cenário 3 (falha/compensação) executado; estado final consistente.
- [ ] Cenário 4 (timeout/resiliência) executado; sem travar o sistema.
- [ ] Cenário 5 (GET status) executado; latência dentro do esperado.

Para requisitos funcionais e não funcionais completos, consulte **REQUIREMENTS.md** e **ARCHITECTURE.md**.
