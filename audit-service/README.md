# Audit Service

Serviço de **auditoria** no fluxo SAGA: consome eventos de transferência enviados pelo orquestrador ou por outros serviços, persiste registros de auditoria em **MongoDB** e publica confirmação via Kafka. Atende requisitos de rastreabilidade, conformidade e análise de eventos de negócio.

---

## Papel no fluxo SAGA

- **Posição:** Pode ser acionado em um ou mais pontos do fluxo (ex.: ao iniciar transferência, ao completar, ao falhar). O orquestrador ou os próprios serviços publicam em `audit.record` para registro centralizado.
- **Entrada:** Comando no tópico `audit.record` com payload do evento (transferId, etapa, estado, dados relevantes, timestamp).
- **Saída:** Publica `audit.recorded` após persistir o registro, permitindo que consumidores saibam que o evento foi auditado.
- **Persistência:** Documentos `AuditLog` no MongoDB, com retenção e consultas alinhadas a políticas de auditoria (diferente de logs operacionais, que podem ir para Loki/ELK conforme ARCHITECTURE.md).

Este serviço não orquestra; apenas registra eventos e emite confirmação.

---

## Responsabilidades

- **Registrar eventos:** Consumir o tópico `audit.record` e persistir cada evento como documento de auditoria em MongoDB.
- **Estrutura do registro:** Armazenar transferId, tipo de evento, estado da saga, payload relevante e timestamp para consultas e relatórios.
- **Publicar confirmação:** Enviar `audit.recorded` com o identificador da transferência (e opcionalmente do registro) para o orquestrador ou outros sistemas.
- **Rastreabilidade:** Suporte a relatórios de auditoria e conformidade regulatória; MongoDB usado exclusivamente para auditoria e eventos de negócio (logs operacionais em ferramenta dedicada).
- **Desempenho:** Processamento assíncrono para não impactar a latência do fluxo principal da saga.

---

## Tópicos Kafka

| Direção | Tópico             | Uso                                           |
|---------|--------------------|-----------------------------------------------|
| Consome | `audit.record`     | Comando para registrar evento de auditoria.   |
| Produz  | `audit.recorded`   | Confirmação de registro persistido.           |

---

## Stack e dependências

- **Java 21**, **Spring Boot 3**, **Spring Data MongoDB**, **Spring Kafka**
- **MongoDB:** coleção de logs de auditoria (`AuditLog`)
- **Apache Kafka:** recebimento de eventos e publicação de confirmação

Classe principal: `com.saga.audit.AuditServiceApplication`.

---

## Build e execução

```bash
mvn clean install
cd audit-service
mvn spring-boot:run
```

**Porta padrão:** 8089. Requer MongoDB e Kafka.
