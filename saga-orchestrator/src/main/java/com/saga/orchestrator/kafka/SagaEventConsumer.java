package com.saga.orchestrator.kafka;

import com.saga.orchestrator.constants.KafkaConstants;
import com.saga.common.dto.SagaEvent;
import com.saga.orchestrator.service.SagaOrchestratorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SagaEventConsumer {

    private final SagaOrchestratorService orchestratorService;

    public SagaEventConsumer(SagaOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_ACCOUNT_VALIDATED)
    public void onAccountValidated(SagaEvent event) {
        orchestratorService.handleAccountValidated(event);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_COMPLIANCE_APPROVED)
    public void onComplianceApproved(SagaEvent event) {
        orchestratorService.handleComplianceApproved(event);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_CURRENCY_CONVERTED)
    public void onCurrencyConverted(SagaEvent event) {
        orchestratorService.handleCurrencyConverted(event);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_TRANSACTION_DEBITED)
    public void onTransactionDebited(SagaEvent event) {
        orchestratorService.handleTransactionDebited(event);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_TRANSACTION_CREDITED)
    public void onTransactionCredited(SagaEvent event) {
        orchestratorService.handleTransactionCredited(event);
    }

    @KafkaListener(topics = {KafkaConstants.TOPIC_TRANSFER_FAILED, KafkaConstants.TOPIC_TRANSFER_COMPENSATED})
    public void onFailureOrCompensation(SagaEvent event) {
        orchestratorService.handleFailureOrCompensation(event);
    }
}
