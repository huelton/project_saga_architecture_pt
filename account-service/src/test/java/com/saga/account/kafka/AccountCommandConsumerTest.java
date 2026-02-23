package com.saga.account.kafka;

import com.saga.account.constants.TestConstants;
import com.saga.account.service.AccountOrchestrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountCommandConsumerTest {

    @Mock
    private AccountOrchestrationService orchestrationService;

    @InjectMocks
    private AccountCommandConsumer consumer;

    @Test
    void onValidateOriginDelegatesToService() {
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_ORIGIN_ACCOUNT_ID, TestConstants.ORIGIN_ACCOUNT_ID
        );
        consumer.onValidateOrigin(payload);
        verify(orchestrationService).validateOrigin(payload);
    }

    @Test
    void onValidateDestinationDelegatesToService() {
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_DESTINATION_ACCOUNT_ID, TestConstants.DESTINATION_ACCOUNT_ID
        );
        consumer.onValidateDestination(payload);
        verify(orchestrationService).validateDestination(payload);
    }
}
