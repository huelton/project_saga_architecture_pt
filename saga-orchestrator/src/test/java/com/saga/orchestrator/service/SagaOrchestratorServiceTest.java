package com.saga.orchestrator.service;

import com.saga.orchestrator.constants.TestConstants;
import com.saga.common.dto.SagaEvent;
import com.saga.orchestrator.entity.SagaInstance;
import com.saga.orchestrator.kafka.SagaKafkaProducer;
import com.saga.orchestrator.repository.SagaInstanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SagaOrchestratorServiceTest {

    @Mock
    private SagaInstanceRepository repository;

    @Mock
    private SagaKafkaProducer kafkaProducer;

    @InjectMocks
    private SagaOrchestratorService service;

    private static SagaEvent newEvent(String transferId) {
        SagaEvent e = new SagaEvent();
        e.setTransferId(transferId);
        return e;
    }

    @Test
    void startTransferSavesInstanceAndSendsToAccountValidateOrigin() {
        when(repository.save(any(SagaInstance.class))).thenAnswer(inv -> inv.getArgument(0));
        SagaInstance result = service.startTransfer(
            TestConstants.TRANSFER_ID_1,
            TestConstants.ORIGIN_ACCOUNT_ID,
            TestConstants.DESTINATION_ACCOUNT_ID,
            TestConstants.AMOUNT_100,
            TestConstants.CURRENCY_USD
        );
        assertNotNull(result);
        verify(repository).save(any(SagaInstance.class));
        verify(kafkaProducer).sendToAccountValidateOrigin(eq(TestConstants.TRANSFER_ID_1), any());
    }

    @Test
    void handleAccountValidatedUpdatesStateAndSendsToCompliance() {
        SagaInstance inst = new SagaInstance();
        inst.setTransferId(TestConstants.TRANSFER_ID_1);
        when(repository.findByTransferId(TestConstants.TRANSFER_ID_1)).thenReturn(Optional.of(inst));
        when(repository.save(any(SagaInstance.class))).thenReturn(inst);
        SagaEvent event = newEvent(TestConstants.TRANSFER_ID_1);
        service.handleAccountValidated(event);
        verify(repository).save(inst);
        verify(kafkaProducer).sendToComplianceValidate(eq(TestConstants.TRANSFER_ID_1), eq(event));
    }

    @Test
    void handleComplianceApprovedUpdatesStateAndSendsToCurrencyConvert() {
        SagaInstance inst = new SagaInstance();
        when(repository.findByTransferId(TestConstants.TRANSFER_ID_1)).thenReturn(Optional.of(inst));
        SagaEvent event = newEvent(TestConstants.TRANSFER_ID_1);
        service.handleComplianceApproved(event);
        verify(repository).save(inst);
        verify(kafkaProducer).sendToCurrencyConvert(eq(TestConstants.TRANSFER_ID_1), eq(event));
    }

    @Test
    void handleCurrencyConvertedUpdatesStateAndSendsToTransactionDebit() {
        SagaInstance inst = new SagaInstance();
        when(repository.findByTransferId(TestConstants.TRANSFER_ID_1)).thenReturn(Optional.of(inst));
        SagaEvent event = newEvent(TestConstants.TRANSFER_ID_1);
        service.handleCurrencyConverted(event);
        verify(repository).save(inst);
        verify(kafkaProducer).sendToTransactionDebit(eq(TestConstants.TRANSFER_ID_1), eq(event));
    }

    @Test
    void handleTransactionDebitedUpdatesStateAndSendsToTransactionCredit() {
        SagaInstance inst = new SagaInstance();
        when(repository.findByTransferId(TestConstants.TRANSFER_ID_1)).thenReturn(Optional.of(inst));
        SagaEvent event = newEvent(TestConstants.TRANSFER_ID_1);
        service.handleTransactionDebited(event);
        verify(repository).save(inst);
        verify(kafkaProducer).sendToTransactionCredit(eq(TestConstants.TRANSFER_ID_1), eq(event));
    }

    @Test
    void handleTransactionCreditedUpdatesStateToCompleted() {
        SagaInstance inst = new SagaInstance();
        when(repository.findByTransferId(TestConstants.TRANSFER_ID_1)).thenReturn(Optional.of(inst));
        SagaEvent event = newEvent(TestConstants.TRANSFER_ID_1);
        service.handleTransactionCredited(event);
        verify(repository).save(inst);
    }

    @Test
    void handleFailureOrCompensationUpdatesStateToFailed() {
        SagaInstance inst = new SagaInstance();
        when(repository.findByTransferId(TestConstants.TRANSFER_ID_1)).thenReturn(Optional.of(inst));
        SagaEvent event = newEvent(TestConstants.TRANSFER_ID_1);
        service.handleFailureOrCompensation(event);
        verify(repository).save(inst);
    }

    @Test
    void getStatusReturnsOptionalFromRepository() {
        SagaInstance inst = new SagaInstance();
        inst.setTransferId(TestConstants.TRANSFER_ID_1);
        when(repository.findByTransferId(TestConstants.TRANSFER_ID_1)).thenReturn(Optional.of(inst));
        Optional<SagaInstance> result = service.getStatus(TestConstants.TRANSFER_ID_1);
        assertNotNull(result);
        assertEquals(TestConstants.TRANSFER_ID_1, result.get().getTransferId());
    }
}
