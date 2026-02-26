# Estratégia de Commits — Projeto SAGA

Este documento descreve a estratégia de commits para o repositório, **começando do zero**. Novos commits devem ser **acrescentados conforme a demanda**, mantendo sempre a **ordem cronológica do desenvolvimento** ao final do arquivo de commits (`commits.json`).

## Objetivo

- Commits em lotes lógicos (~3h de trabalho cada).
- Intervalo entre envios: **2 horas** + variação aleatória de **10 a 30 minutos** (jitter).
- Horário de referência: **horário local da máquina**.
- Sem uso de co-author nos commits.
- Reproduzível em outros projetos.

## Uso do script

- **Modo manual:** executar o script para enviar apenas o próximo commit.
- **Modo agendado:** o script agenda o próximo envio em 2h + jitter e repete até acabar a lista.
- O script consulta o último commit já enviado (estado em `.strategy_state.json`) e **nunca reenvia** commits já enviados.

Arquivo de dados: **`commits.json`** na raiz (lista de `message` e `files` por commit).

## Regras

1. **Um commit por vez:** o script envia sempre o próximo commit da lista.
2. **Horário local:** cálculo de “próxima execução” usa horário local.
3. **Jitter:** intervalo entre envios = 2h + aleatório (10–30 min).
4. **Sem co-author:** commits apenas com `git commit -m "..."`.
5. **Estado persistente:** progresso em `.strategy_state.json` (fora do controle de versão).
6. **Ordem cronológica:** ao adicionar novos commits, insira-os **ao final** de `commits.json` para refletir a ordem real do desenvolvimento.

## Sincronizando com histórico existente

Se o repositório já tiver commits e você quiser continuar a partir da lista:

1. Abra `.strategy_state.json` (crie na raiz se não existir).
2. Defina `last_pushed_index` como o **índice (0-based) do último commit já enviado** na ordem da lista.
3. Salve e rode o script; o próximo envio será o item seguinte.

## Ordem cronológica dos commits (desenvolvimento do zero)

A lista em **`commits.json`** segue a ordem cronológica do desenvolvimento. Resumo:

1. **Setup e documentação inicial** — repositório, README, documentação de arquitetura e requisitos.
2. **Infraestrutura** — docker-compose (dados, admin, depois observabilidade).
3. **Módulos shared** — saga-common, kafka-common, circuit-breaker (conforme ARCHITECTURE.md).
4. **Observabilidade** — Prometheus, Loki, Grafana no docker-compose; configurações; OBSERVABILITY.md.
5. **Testes de carga** — documentação JMeter e plano de teste exemplo (JMETER.md, JMX).
6. **Saga Orchestrator** — módulo, uso de saga-common, API, Kafka, testes.
7. **Microsserviços** — account, validation, currency, transaction, notification, audit (cada um com estrutura, Kafka, testes).
8. **Documentação e scripts** — TESTING.md, estratégia de commits, script de agendamento, READMEs dos serviços.

**Novos commits:** ao evoluir o projeto, acrescente novas entradas **ao final** de `commits.json` e, se necessário, atualize esta seção com o resumo da nova fase, mantendo a ordem cronológica.
