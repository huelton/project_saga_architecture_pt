package com.saga.notification.kafka;

import com.saga.notification.constants.TestConstants;
import com.saga.notification.service.NotificationSenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationCommandConsumerTest {

    @Mock
    private NotificationSenderService senderService;

    @InjectMocks
    private NotificationCommandConsumer consumer;

    @Test
    void onSendDelegatesToService() {
        Map<String, Object> payload = Map.of(TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1);
        consumer.onSend(payload);
        verify(senderService).send(payload);
    }
}
