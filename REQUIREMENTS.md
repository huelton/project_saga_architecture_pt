# Requisitos do Sistema

## Requisitos Funcionais

### RF01 - Iniciar Transferência Internacional
**Descrição**: O sistema deve permitir que um cliente inicie uma transferência bancária internacional.

**Prioridade**: Alta

**Critérios de Aceitação**:
- Cliente deve fornecer: conta origem, conta destino, valor, moedas
- Sistema deve validar dados de entrada
- Sistema deve criar instância de SAGA
- Sistema deve retornar ID da transferência

**Entrada**:
```json
{
  "originAccount": "BR123456789",
  "destinationAccount": "US987654321",
  "amount": 1000.00,
  "originCurrency": "BRL",
  "destinationCurrency": "USD",
  "clientId": "client-001"
}
```

**Saída**:
```json
{
  "sagaId": "saga-12345",
  "transferId": "transfer-12345",
  "status": "PENDING",
  "createdAt": "2024-01-15T10:30:00Z"
}
```

### RF02 - Validar Conta Origem
**Descrição**: Sistema deve validar se a conta origem existe, está ativa e possui saldo suficiente.

**Prioridade**: Alta

**Critérios de Aceitação**:
- Verificar existência da conta
- Verificar se conta está ativa
- Verificar saldo disponível
- Verificar limites diários/mensais
- Retornar erro se alguma validação falhar

### RF03 - Validar Conta Destino
**Descrição**: Sistema deve validar se a conta destino existe e está operacional.

**Prioridade**: Alta

**Critérios de Aceitação**:
- Verificar existência da conta
- Validar dados bancários (SWIFT, IBAN)
- Verificar se banco destino está operacional
- Retornar erro se validação falhar

### RF04 - Validar Compliance
**Descrição**: Sistema deve validar regras de compliance e regulamentações.

**Prioridade**: Alta

**Critérios de Aceitação**:
- Verificar limites regulatórios
- Executar verificação AML (Anti-Lavagem de Dinheiro)
- Verificar lista de sanções internacionais
- Registrar tentativas suspeitas
- Bloquear transferências não conformes

### RF05 - Converter Moeda
**Descrição**: Sistema deve converter valor da moeda origem para moeda destino.

**Prioridade**: Alta

**Critérios de Aceitação**:
- Consultar taxa de câmbio atual
- Calcular valor convertido
- Reservar taxa por período limitado (5 minutos)
- Permitir cancelamento de reserva

### RF06 - Executar Débito
**Descrição**: Sistema deve debitar valor da conta origem.

**Prioridade**: Alta

**Critérios de Aceitação**:
- Bloquear saldo na conta origem
- Registrar transação pendente
- Garantir idempotência
- Permitir compensação em caso de falha

### RF07 - Executar Crédito
**Descrição**: Sistema deve creditar valor na conta destino.

**Prioridade**: Alta

**Critérios de Aceitação**:
- Creditar valor convertido na conta destino
- Confirmar transação
- Garantir idempotência
- Permitir compensação em caso de falha

### RF08 - Enviar Notificações
**Descrição**: Sistema deve enviar notificações aos clientes.

**Prioridade**: Média

**Critérios de Aceitação**:
- Enviar email para cliente origem
- Enviar email para cliente destino
- Enviar SMS (opcional)
- Notificar sistemas internos
- Retry em caso de falha

### RF09 - Registrar Auditoria
**Descrição**: Sistema deve registrar todos os eventos para auditoria.

**Prioridade**: Alta

**Critérios de Aceitação**:
- Registrar todas as etapas da transferência
- Armazenar logs de compliance
- Manter histórico por período legal (5 anos)
- Permitir consulta e relatórios

### RF10 - Compensação Automática
**Descrição**: Sistema deve executar compensação automática em caso de falha.

**Prioridade**: Alta

**Critérios de Aceitação**:
- Detectar falha em qualquer etapa
- Executar compensações na ordem inversa
- Reverter todas as operações realizadas
- Notificar cliente sobre falha
- Registrar motivo da falha

### RF11 - Consultar Status de Transferência
**Descrição**: Sistema deve permitir consulta do status de uma transferência.

**Prioridade**: Média

**Critérios de Aceitação**:
- Retornar estado atual do SAGA
- Retornar histórico de etapas executadas
- Retornar informações de erro se houver
- Suportar polling ou webhook

### RF12 - Circuit Breaker
**Descrição**: Sistema deve implementar Circuit Breaker para proteção contra falhas.

**Prioridade**: Alta

**Critérios de Aceitação**:
- Abrir circuito após 5 falhas consecutivas
- Bloquear requisições quando circuito aberto
- Tentar recuperação após timeout
- Retornar fallback quando circuito aberto
- Registrar métricas de circuito

## Requisitos Não Funcionais

### RNF01 - Performance
- **Latência**: Transferência completa em < 30 segundos (95th percentile)
- **Throughput**: Suportar 1000 transferências/minuto
- **Tempo de resposta**: API Gateway < 100ms
- **Processamento assíncrono**: Operações não bloqueantes

### RNF02 - Disponibilidade
- **Uptime**: 99.9% (8.76 horas de downtime/ano)
- **Redundância**: Múltiplas instâncias de cada serviço
- **Failover**: Automático e transparente
- **Recuperação**: Rápida após falhas

### RNF03 - Escalabilidade
- **Horizontal**: Escalar serviços independentemente
- **Elástica**: Auto-scaling baseado em carga
- **Particionamento**: Kafka particionado para paralelismo
- **Cache**: Cache distribuído para reduzir carga

### RNF04 - Confiabilidade
- **Idempotência**: Operações idempotentes
- **At-least-once delivery**: Kafka com garantias
- **Transações**: Compensação garantida
- **Retry**: Retry automático com backoff

### RNF05 - Segurança
- **Autenticação**: JWT obrigatório
- **Autorização**: RBAC
- **Criptografia**: TLS 1.3 em trânsito, AES-256 em repouso
- **Validação**: Validação rigorosa de entrada
- **Rate Limiting**: Proteção contra abuso
- **Logs**: Sem dados sensíveis em logs

### RNF06 - Observabilidade
- **Métricas**: Prometheus com métricas customizadas
- **Logs**: Estruturados (JSON) com correlation ID
- **Tracing**: Distributed tracing (Jaeger)
- **Alertas**: Alertas proativos para problemas
- **Dashboards**: Dashboards em tempo real

### RNF07 - Manutenibilidade
- **Código**: Código limpo e documentado
- **Testes**: Cobertura > 80%
- **Documentação**: Documentação completa
- **Versionamento**: Versionamento de APIs
- **CI/CD**: Pipeline automatizado

### RNF08 - Compliance
- **LGPD/GDPR**: Proteção de dados pessoais
- **PCI-DSS**: Segurança de dados financeiros
- **Regulamentações**: Conformidade com regulamentações bancárias
- **Auditoria**: Logs auditáveis por 5 anos
- **Relatórios**: Relatórios de compliance

### RNF09 - Resiliência
- **Circuit Breaker**: Proteção contra falhas em cascata
- **Timeout**: Timeouts configuráveis
- **Fallback**: Estratégias de fallback
- **Bulkhead**: Isolamento de recursos
- **Chaos Engineering**: Testes de resiliência

### RNF10 - Usabilidade
- **API**: API RESTful intuitiva
- **Documentação**: Swagger/OpenAPI
- **Erros**: Mensagens de erro claras
- **Versionamento**: Versionamento de API

## Casos de Uso

### CU01 - Transferência Bem-Sucedida
**Ator**: Cliente

**Fluxo Principal**:
1. Cliente solicita transferência internacional
2. Sistema valida conta origem
3. Sistema valida conta destino
4. Sistema valida compliance
5. Sistema converte moeda
6. Sistema debita conta origem
7. Sistema credita conta destino
8. Sistema envia notificações
9. Sistema registra auditoria
10. Cliente recebe confirmação

**Resultado**: Transferência concluída com sucesso

### CU02 - Transferência com Falha de Saldo
**Ator**: Cliente

**Fluxo Principal**:
1. Cliente solicita transferência internacional
2. Sistema valida conta origem
3. Sistema detecta saldo insuficiente
4. Sistema cancela transferência
5. Sistema notifica cliente sobre falha

**Resultado**: Transferência cancelada, cliente notificado

### CU03 - Transferência com Falha em Crédito
**Ator**: Cliente

**Fluxo Principal**:
1. Cliente solicita transferência internacional
2. Sistema executa todas as validações
3. Sistema converte moeda
4. Sistema debita conta origem
5. Sistema tenta creditar conta destino
6. Sistema detecta falha no crédito
7. Sistema executa compensação:
   - Reverte débito na conta origem
   - Cancela reserva de moeda
8. Sistema notifica cliente sobre falha

**Resultado**: Transferência cancelada, débito revertido

### CU04 - Transferência Bloqueada por Compliance
**Ator**: Cliente

**Fluxo Principal**:
1. Cliente solicita transferência internacional
2. Sistema valida conta origem
3. Sistema valida conta destino
4. Sistema valida compliance
5. Sistema detecta violação de compliance (ex: sanção)
6. Sistema bloqueia transferência
7. Sistema registra tentativa suspeita
8. Sistema notifica cliente (mensagem genérica)
9. Sistema notifica equipe de compliance

**Resultado**: Transferência bloqueada, compliance notificado

### CU05 - Consulta de Status
**Ator**: Cliente

**Fluxo Principal**:
1. Cliente consulta status da transferência
2. Sistema busca estado do SAGA
3. Sistema retorna status atual e histórico

**Resultado**: Cliente visualiza status da transferência

## Regras de Negócio

### RN01 - Limites de Transferência
- Limite diário por conta: R$ 50.000,00
- Limite mensal por conta: R$ 500.000,00
- Limite por transação: R$ 10.000,00 (sem aprovação adicional)
- Transações acima de R$ 10.000,00 requerem aprovação manual

### RN02 - Taxa de Câmbio
- Taxa reservada por 5 minutos
- Taxa obtida de API externa
- Cache de taxa por 1 minuto
- Spread de 2% aplicado

### RN03 - Compensação
- Compensação deve ser executada em até 1 minuto após falha
- Todas as operações devem ser reversíveis
- Compensação deve ser idempotente

### RN04 - Notificações
- Email obrigatório para ambas as partes
- SMS opcional (cliente deve optar)
- Retry de notificação: 3 tentativas com intervalo de 5 minutos

### RN05 - Auditoria
- Todos os eventos devem ser registrados
- Logs devem ser mantidos por 5 anos
- Logs devem ser imutáveis após criação

### RN06 - Compliance
- Verificação AML obrigatória para valores > R$ 3.000,00
- Verificação de sanções obrigatória
- Bloqueio automático de transferências suspeitas

## Infraestrutura e Persistência

- **PostgreSQL (SQL)**: Estado do SAGA, contas, transações. Uso para dados transacionais e consistência forte.
- **MongoDB (NoSQL)**: Logs de aplicação, auditoria e eventos de negócio. Retenção conforme política de compliance.
- **Redis**: Cache de taxas de câmbio (Currency Service), limites de conta e sessões. TTL conforme regras de negócio.
- **Kafka**: Mensageria entre orquestrador e microsserviços; infra disponível no `docker-compose.yml`.
- **Logs centralizados**: Recomenda-se Grafana Loki (recomendado) ou ELK Stack para logs operacionais; MongoDB para auditoria/eventos de negócio.

## Restrições

### RE01 - Tecnológicas
- Java 17+ ou Node.js 18+
- Kafka 3.x (docker-compose)
- PostgreSQL 14+ (docker-compose)
- MongoDB 6+ (docker-compose)
- Redis 7+ (docker-compose)

### RE02 - Infraestrutura
- Kubernetes para produção
- Docker para containerização
- Cloud provider (AWS, Azure, GCP)

### RE03 - Regulatórias
- Conformidade com LGPD
- Conformidade com regulamentações bancárias
- Retenção de dados por 5 anos

### RE04 - Operacionais
- Deploy apenas em horários de manutenção
- Backup diário de bancos de dados
- Monitoramento 24/7
