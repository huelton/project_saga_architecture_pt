# SAGA Orchestrator

Orquestrador central do padrão **SAGA Orquestrado** para transferências bancárias internacionais. Responsável por iniciar o fluxo, coordenar cada etapa na ordem definida e atualizar o estado da saga; em caso de falha, consome eventos de compensação e persiste o estado final.

---

## Papel no fluxo SAGA

Este serviço é o **ponto de entrada** e o **coordenador** do fluxo:

1. **Início:** Recebe `POST /api/transfers`, gera um `transferId`, persiste uma instância da saga em estado `VALIDATING_ORIGIN` e envia o comando para validação da conta de origem.
2. **Sequência:** A cada evento recebido (conta validada, compliance aprovado, moeda convertida, débito realizado, crédito realizado), avança o estado e dispara o próximo comando até concluir ou falhar.
3. **Fim:** Atualiza o estado para `COMPLETED` após o crédito na conta destino, ou para `FAILED` ao receber eventos de falha/compensação.
4. **Consulta:** Oferece `GET /api/transfers/{transferId}/status` para acompanhamento do status da transferência.

Nenhum outro serviço orquestra o fluxo; todos reagem a comandos e publicam eventos.

---

## Responsabilidades

- **Gerenciar estado da saga:** Persistir `SagaInstance` (e opcionalmente `SagaStep`) em PostgreSQL, com estados alinhados a `SagaConstants`.
- **Publicar comandos:** Enviar mensagens para os tópicos de comando (account validate origin, compliance validate, currency convert, transaction debit/credit) com payload `SagaEvent` (contrato em `saga-common`).
- **Consumir eventos:** Ouvir os tópicos de evento (account.validated, compliance.approved, currency.converted, transaction.debited, transaction.credited, transfer.failed, transfer.compensated) e delegar ao `SagaOrchestratorService`.
- **API REST:** Expor endpoints para iniciar transferência e consultar status; uso de DTOs compartilhados (`TransferRequest`, `TransferResponse`) do módulo `saga-common`.

---

## Tópicos Kafka

| Direção   | Tópico                    | Uso                                                                 |
|----------|---------------------------|---------------------------------------------------------------------|
| Produz   | `account.validate.origin` | Comando para validar conta de origem (primeiro passo após iniciar). |
| Produz   | `compliance.validate`     | Comando para validar compliance após conta validada.                |
| Produz   | `currency.convert`       | Comando para conversão de moeda após compliance aprovado.          |
| Produz   | `transaction.debit`       | Comando para débito após conversão.                                 |
| Produz   | `transaction.credit`      | Comando para crédito após débito.                                   |
| Consome  | `account.validated`       | Avançar para validação de compliance.                              |
| Consome  | `compliance.approved`     | Avançar para conversão de moeda.                                    |
| Consome  | `currency.converted`     | Avançar para débito.                                                |
| Consome  | `transaction.debited`     | Avançar para crédito.                                               |
| Consome  | `transaction.credited`   | Marcar saga como concluída.                                         |
| Consome  | `transfer.failed`, `transfer.compensated` | Marcar saga como falha/compensação.                    |

---

## API

| Método | Path                          | Descrição                                                                 |
|--------|-------------------------------|---------------------------------------------------------------------------|
| POST   | `/api/transfers`              | Inicia uma transferência. Body: `originAccountId`, `destinationAccountId`, `amount`, `currency`. Resposta 202 com identificador e status `STARTED`. |
| GET    | `/api/transfers/{transferId}/status` | Retorna o status atual da saga (ex.: VALIDATING_ORIGIN, COMPLETED, FAILED). 404 se não existir. |

---

## Stack e dependências

- **Java 21**, **Spring Boot 3**, **Spring Data JPA**, **Spring Kafka**
- **PostgreSQL:** persistência de `SagaInstance` (e `SagaStep` se utilizado)
- **Apache Kafka:** mensageria com os demais microsserviços
- **saga-common:** DTOs `SagaEvent`, `TransferRequest`, `TransferResponse`

Classe principal: `com.saga.orchestrator.SagaOrchestratorApplication`.

---

## Build e execução

Recomenda-se fazer o build a partir da **raiz do repositório** para instalar o módulo `saga-common`:

```bash
# Na raiz do projeto
mvn clean install
cd saga-orchestrator
mvn spring-boot:run
```

**Porta padrão:** 8083. Requer PostgreSQL e Kafka em execução (ex.: `docker-compose up -d` na raiz).
