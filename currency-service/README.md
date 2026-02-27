# Currency Service

Serviço de **conversão de moedas** no fluxo SAGA: recebe o pedido de conversão com valor e moedas de origem/destino, obtém a taxa (com cache em **Redis** quando configurado), calcula o valor convertido e publica o resultado para o orquestrador.

---

## Papel no fluxo SAGA

- **Posição:** Acionado após a aprovação de compliance (estado `CONVERTING_CURRENCY` no orquestrador).
- **Entrada:** Comando no tópico `currency.convert` com dados da transferência (transferId, valor, moeda origem, moeda destino).
- **Saída:** Publica `currency.converted` com o valor convertido (e taxa utilizada, se aplicável) para o orquestrador seguir para a etapa de débito.
- **Cache:** Uso de Redis para cache de taxas de câmbio (TTL configurável), reduzindo chamadas a APIs externas e melhorando latência.

Este serviço não orquestra; apenas converte e notifica o resultado.

---

## Responsabilidades

- **Converter valor:** Calcular o valor na moeda de destino com base na taxa de câmbio (cache Redis ou valor padrão).
- **Consumir comando:** Ouvir o tópico `currency.convert` e extrair valor e moedas do payload.
- **Publicar resultado:** Enviar `currency.converted` com o valor convertido e dados necessários para as próximas etapas (débito/crédito).
- **Cache de taxas:** Configuração de Redis para armazenar taxas com TTL; fallback para taxa padrão quando Redis não estiver disponível ou chave ausente.
- **Extensibilidade:** Preparado para integração com API de câmbio externa e Circuit Breaker (ex.: módulo `circuit-breaker` ou Resilience4j).

---

## Tópicos Kafka

| Direção | Tópico               | Uso                                      |
|---------|----------------------|------------------------------------------|
| Consome | `currency.convert`   | Comando para converter valor entre moedas. |
| Produz  | `currency.converted` | Resultado da conversão para o orquestrador. |

---

## Stack e dependências

- **Java 21**, **Spring Boot 3**, **Spring Data Redis**, **Spring Kafka**
- **Redis:** cache de taxas de câmbio
- **Apache Kafka:** comunicação com o orquestrador

Classe principal: `com.saga.currency.CurrencyServiceApplication`.

---

## Build e execução

```bash
mvn clean install
cd currency-service
mvn spring-boot:run
```

**Porta padrão:** 8086. Requer Redis e Kafka.
