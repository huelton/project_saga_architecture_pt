# Account Service

Serviço de **contas bancárias** no fluxo SAGA: valida existência e elegibilidade das contas de origem e destino e publica o resultado para o orquestrador. Também dá suporte às operações de débito e crédito quando acionado pelos comandos de transação (via Transaction Service no fluxo atual). Persistência em **PostgreSQL**.

---

## Papel no fluxo SAGA

- **Posição:** Primeiro serviço acionado pelo orquestrador após o início da transferência (estado `VALIDATING_ORIGIN`).
- **Entrada:** Comando no tópico `account.validate.origin` (e, em fluxos estendidos, `account.validate.destination`) com dados da transferência (transferId, contas, valor, moeda).
- **Saída:** Publica no tópico `account.validated` para o orquestrador avançar para a etapa de compliance.
- **Dados:** Mantém entidade `Account` (identificador, saldo, etc.); o `DataInitializer` cria contas de exemplo (ex.: ACC-001, ACC-002) quando o banco está vazio, para uso em testes e demonstração.

Este serviço não decide a ordem do fluxo; apenas processa o comando de validação e emite o evento correspondente.

---

## Responsabilidades

- **Validar contas:** Verificar existência e condições das contas de origem e destino conforme regras de negócio.
- **Consumir comandos:** Ouvir `account.validate.origin` (e opcionalmente `account.validate.destination`) e processar a mensagem.
- **Publicar resultado:** Enviar evento `account.validated` com o mesmo `transferId` e dados necessários para o próximo passo.
- **Suporte a débito/crédito:** Preparado para operações de movimentação quando integrado aos comandos de transação (transaction.debit / transaction.credit).
- **Persistência:** Spring Data JPA com repositório de contas em PostgreSQL.

---

## Tópicos Kafka

| Direção  | Tópico                      | Uso                                              |
|----------|-----------------------------|--------------------------------------------------|
| Consome  | `account.validate.origin`   | Comando para validar conta de origem.            |
| Consome  | `account.validate.destination` | Comando para validar conta de destino (se usado). |
| Produz   | `account.validated`         | Evento de validação concluída para o orquestrador. |

---

## Stack e dependências

- **Java 21**, **Spring Boot 3**, **Spring Data JPA**, **Spring Kafka**
- **PostgreSQL:** armazenamento de contas
- **Apache Kafka:** comunicação com o orquestrador

Classe principal: `com.saga.account.AccountServiceApplication`.

---

## Build e execução

```bash
# Na raiz do projeto (para instalar dependências compartilhadas, se houver)
mvn clean install
cd account-service
mvn spring-boot:run
```

**Porta padrão:** 8084. Requer PostgreSQL e Kafka.
