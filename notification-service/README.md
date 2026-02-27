# Notification Service

Serviço de **notificações** no fluxo SAGA: consome pedidos de envio de notificação via Kafka (ex.: confirmação de transferência para o cliente) e publica confirmação de envio. Na versão atual, o envio é **simulado** (sem integração real com e-mail/SMS/push), permitindo validar o fluxo end-to-end.

---

## Papel no fluxo SAGA

- **Posição:** Pode ser acionado pelo orquestrador após a conclusão da transferência (estado `COMPLETED`) ou em pontos definidos pela regra de negócio (ex.: notificação de “transferência iniciada” ou “transferência concluída”).
- **Entrada:** Comando no tópico `notification.send` com dados da transferência e do destinatário (transferId, tipo de notificação, dados do cliente).
- **Saída:** Publica `notification.sent` para sinalizar que o pedido foi processado (e, em cenários de falha, pode publicar `notification.failed` para retry ou DLQ).
- **Desacoplamento:** Não bloqueia o fluxo principal da saga; o orquestrador pode considerar a saga concluída assim que receber `transaction.credited` e disparar a notificação de forma assíncrona.

Este serviço não orquestra; apenas processa o comando de notificação e emite o evento correspondente.

---

## Responsabilidades

- **Processar pedidos de notificação:** Consumir mensagens do tópico `notification.send` e executar a lógica de envio (simulada ou real).
- **Publicar confirmação:** Enviar `notification.sent` com o mesmo `transferId` para o orquestrador ou outros consumidores.
- **Simulação:** Implementação atual simula o envio (log ou operação in-memory) para desenvolvimento e testes sem dependências externas.
- **Extensibilidade:** Preparado para integração com provedores de e-mail, SMS ou push e para Circuit Breaker e retry em caso de falha temporária.

---

## Tópicos Kafka

| Direção | Tópico               | Uso                                      |
|---------|----------------------|------------------------------------------|
| Consome | `notification.send`  | Comando para enviar notificação.         |
| Produz  | `notification.sent`  | Confirmação de envio processado.         |
| Produz  | `notification.failed`| Falha no envio (se implementado para retry/DLQ). |

---

## Stack e dependências

- **Java 21**, **Spring Boot 3**, **Spring Kafka**
- **Apache Kafka:** comunicação com o orquestrador (sem banco de dados próprio na versão atual)

Classe principal: `com.saga.notification.NotificationServiceApplication`.

---

## Build e execução

```bash
mvn clean install
cd notification-service
mvn spring-boot:run
```

**Porta padrão:** 8088. Requer Kafka.
