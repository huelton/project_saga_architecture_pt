package com.saga.orchestrator.kafka;

import com.saga.orchestrator.constants.TestConstants;
import com.saga.common.dto.SagaEvent;
import com.saga.orchestrator.service.SagaOrchestratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SagaEventConsumerTest {

    @Mock
    private SagaOrchestratorService orchestratorService;

    @InjectMocks
    private SagaEventConsumer consumer;

    @Test
    void onAccountValidatedDelegatesToService() {
        SagaEvent event = new SagaEvent();
        event.setTransferId(TestConstants.TRANSFER_ID_1);
        consumer.onAccountValidated(event);
        verify(orchestratorService).handleAccountValidated(event);
    }

    @Test
    void onComplianceApprovedDelegatesToService() {
        SagaEvent event = new SagaEvent();
        event.setTransferId(TestConstants.TRANSFER_ID_1);
        consumer.onComplianceApproved(event);
        verify(orchestratorService).handleComplianceApproved(event);
    }

    @Test
    void onCurrencyConvertedDelegatesToService() {
        SagaEvent event = new SagaEvent();
        event.setTransferId(TestConstants.TRANSFER_ID_1);
        consumer.onCurrencyConverted(event);
        verify(orchestratorService).handleCurrencyConverted(event);
    }

    @Test
    void onTransactionDebitedDelegatesToService() {
        SagaEvent event = new SagaEvent();
        event.setTransferId(TestConstants.TRANSFER_ID_1);
        consumer.onTransactionDebited(event);
        verify(orchestratorService).handleTransactionDebited(event);
    }

    @Test
    void onTransactionCreditedDelegatesToService() {
        SagaEvent event = new SagaEvent();
        event.setTransferId(TestConstants.TRANSFER_ID_1);
        consumer.onTransactionCredited(event);
        verify(orchestratorService).handleTransactionCredited(event);
    }

    @Test
    void onFailureOrCompensationDelegatesToService() {
        SagaEvent event = new SagaEvent();
        event.setTransferId(TestConstants.TRANSFER_ID_1);
        consumer.onFailureOrCompensation(event);
        verify(orchestratorService).handleFailureOrCompensation(event);
    }
}
