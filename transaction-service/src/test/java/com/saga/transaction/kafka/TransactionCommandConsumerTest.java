package com.saga.transaction.kafka;

import com.saga.transaction.constants.TestConstants;
import com.saga.transaction.service.TransactionOrchestrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionCommandConsumerTest {

    @Mock
    private TransactionOrchestrationService orchestrationService;

    @InjectMocks
    private TransactionCommandConsumer consumer;

    @Test
    void onDebitDelegatesToService() {
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_ORIGIN_ACCOUNT_ID, TestConstants.ACCOUNT_ID_1,
            TestConstants.PAYLOAD_KEY_AMOUNT, TestConstants.AMOUNT_100
        );
        consumer.onDebit(payload);
        verify(orchestrationService).debit(payload);
    }

    @Test
    void onCreditDelegatesToService() {
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_DESTINATION_ACCOUNT_ID, TestConstants.ACCOUNT_ID_2,
            TestConstants.PAYLOAD_KEY_AMOUNT, TestConstants.AMOUNT_100
        );
        consumer.onCredit(payload);
        verify(orchestrationService).credit(payload);
    }
}
