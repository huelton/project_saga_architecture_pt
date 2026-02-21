/**
 * Módulo compartilhado de resiliência (Circuit Breaker, Retry).
 * Os serviços importam este módulo e configuram resilience4j via application.yml.
 * Exemplo de instâncias: accountService, currencyService (conforme ARCHITECTURE.md).
 */
package com.saga.common.resilience;
