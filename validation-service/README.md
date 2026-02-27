# Validation Service

Serviço de **compliance e validação regulatória** no fluxo SAGA: executa verificações de conformidade (AML, limites, políticas) para a transferência e registra o resultado em **MongoDB**. Publica aprovação ou rejeição via Kafka para o orquestrador decidir o próximo passo.

---

## Papel no fluxo SAGA

- **Posição:** Acionado após a validação das contas (estado `VALIDATING_COMPLIANCE` no orquestrador).
- **Entrada:** Comando no tópico `compliance.validate` com dados da transferência (transferId, contas, valor, moeda).
- **Saída:** Publica `compliance.approved` (e, em cenários de rejeição, pode publicar `compliance.rejected`) para o orquestrador avançar ou disparar compensação.
- **Persistência:** Registros de compliance em documento `ComplianceLog` no MongoDB, para auditoria e rastreabilidade.

Este serviço não orquestra; apenas avalia a operação e emite o evento de aprovação ou rejeição.

---

## Responsabilidades

- **Validar compliance:** Aplicar regras de anti-lavagem (AML), limites (diário/mensal) e políticas internas sobre a transferência.
- **Consumir comando:** Ouvir o tópico `compliance.validate` e processar o payload.
- **Registrar decisão:** Persistir em MongoDB (`ComplianceLog`) o resultado da validação com identificador da transferência e metadados.
- **Publicar resultado:** Enviar `compliance.approved` ou `compliance.rejected` para o orquestrador.
- **Integração futura:** Preparado para consulta a listas de sanções ou APIs externas de compliance, se necessário.

---

## Tópicos Kafka

| Direção | Tópico                 | Uso                                           |
|---------|------------------------|-----------------------------------------------|
| Consome | `compliance.validate`  | Comando para executar validação de compliance. |
| Produz  | `compliance.approved`  | Aprovação para o orquestrador prosseguir.     |
| Produz  | `compliance.rejected`  | Rejeição (dispara compensação no orquestrador, se implementado). |

---

## Stack e dependências

- **Java 21**, **Spring Boot 3**, **Spring Data MongoDB**, **Spring Kafka**
- **MongoDB:** coleção de logs de compliance
- **Apache Kafka:** comunicação com o orquestrador

Classe principal: `com.saga.validation.ValidationServiceApplication`.

---

## Build e execução

```bash
mvn clean install
cd validation-service
mvn spring-boot:run
```

**Porta padrão:** 8085. Requer MongoDB e Kafka.
