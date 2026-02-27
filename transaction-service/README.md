# Transaction Service

Serviço de **registro e orquestração de transações** (débito e crédito) no fluxo SAGA: persiste cada operação como `TransactionRecord` em **PostgreSQL** e notifica o orquestrador via Kafka. Garante rastreabilidade das etapas de débito e crédito da transferência.

---

## Papel no fluxo SAGA

- **Posição:** Acionado após a conversão de moeda em duas etapas sequenciais: primeiro **débito** na conta de origem (estado `DEBITING`), depois **crédito** na conta de destino (estado `CREDITING`).
- **Entrada:** Comandos nos tópicos `transaction.debit` e `transaction.credit`, com dados da transferência (transferId, contas, valor convertido, etc.).
- **Saída:** Publica `transaction.debited` após registrar o débito e `transaction.credited` após registrar o crédito, permitindo ao orquestrador avançar até a conclusão da saga.
- **Persistência:** Cada operação gera um `TransactionRecord` (transferId, tipo débito/crédito, conta, valor, timestamp) em PostgreSQL.

Este serviço não executa diretamente o movimento de saldo nas contas; coordena o registro da transação e a sinalização para o orquestrador (a movimentação efetiva de saldo fica a cargo do Account Service quando integrado a esses comandos).

---

## Responsabilidades

- **Registrar débito:** Consumir `transaction.debit`, persistir o registro da operação de débito e publicar `transaction.debited`.
- **Registrar crédito:** Consumir `transaction.credit`, persistir o registro da operação de crédito e publicar `transaction.credited`.
- **Manter histórico:** Entidade `TransactionRecord` e repositório JPA para consulta e auditoria.
- **Orquestração interna:** Serviço de orquestração que processa a mensagem, persiste e publica o evento no tópico correto.
- **Consistência:** Uso de transações e identificador de transferência para correlacionar débito e crédito da mesma saga.

---

## Tópicos Kafka

| Direção | Tópico                 | Uso                                           |
|---------|------------------------|-----------------------------------------------|
| Consome | `transaction.debit`    | Comando para registrar e processar o débito.  |
| Consome | `transaction.credit`   | Comando para registrar e processar o crédito.  |
| Produz  | `transaction.debited`  | Evento de débito concluído para o orquestrador. |
| Produz  | `transaction.credited` | Evento de crédito concluído para o orquestrador. |

---

## Stack e dependências

- **Java 21**, **Spring Boot 3**, **Spring Data JPA**, **Spring Kafka**
- **PostgreSQL:** armazenamento de `TransactionRecord`
- **Apache Kafka:** comunicação com o orquestrador

Classe principal: `com.saga.transaction.TransactionServiceApplication`.

---

## Build e execução

```bash
mvn clean install
cd transaction-service
mvn spring-boot:run
```

**Porta padrão:** 8087. Requer PostgreSQL e Kafka.
