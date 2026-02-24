package com.saga.transaction.service;

import com.saga.transaction.constants.TestConstants;
import com.saga.transaction.kafka.TransactionEventProducer;
import com.saga.transaction.repository.TransactionRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionOrchestrationServiceTest {

    @Mock
    private TransactionRecordRepository repository;

    @Mock
    private TransactionEventProducer eventProducer;

    @InjectMocks
    private TransactionOrchestrationService service;

    @Test
    void debitSavesRecordAndSendsDebited() {
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_ORIGIN_ACCOUNT_ID, TestConstants.ACCOUNT_ID_1,
            TestConstants.PAYLOAD_KEY_AMOUNT, TestConstants.AMOUNT_100
        );
        service.debit(payload);
        ArgumentCaptor<com.saga.transaction.entity.TransactionRecord> recordCaptor =
            ArgumentCaptor.forClass(com.saga.transaction.entity.TransactionRecord.class);
        verify(repository).save(recordCaptor.capture());
        assertEquals(TestConstants.TRANSFER_ID_1, recordCaptor.getValue().getTransferId());
        assertEquals(TestConstants.TYPE_DEBIT, recordCaptor.getValue().getType());
        assertEquals(TestConstants.ACCOUNT_ID_1, recordCaptor.getValue().getAccountId());
        verify(eventProducer).sendDebited(eq(TestConstants.TRANSFER_ID_1), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void creditSavesRecordAndSendsCredited() {
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_DESTINATION_ACCOUNT_ID, TestConstants.ACCOUNT_ID_2,
            TestConstants.PAYLOAD_KEY_AMOUNT, TestConstants.AMOUNT_100
        );
        service.credit(payload);
        ArgumentCaptor<com.saga.transaction.entity.TransactionRecord> recordCaptor =
            ArgumentCaptor.forClass(com.saga.transaction.entity.TransactionRecord.class);
        verify(repository).save(recordCaptor.capture());
        assertEquals(TestConstants.TRANSFER_ID_1, recordCaptor.getValue().getTransferId());
        assertEquals(TestConstants.TYPE_CREDIT, recordCaptor.getValue().getType());
        assertEquals(TestConstants.ACCOUNT_ID_2, recordCaptor.getValue().getAccountId());
        verify(eventProducer).sendCredited(eq(TestConstants.TRANSFER_ID_1), org.mockito.ArgumentMatchers.any());
    }
}
