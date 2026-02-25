package com.saga.notification.service;

import com.saga.notification.constants.TestConstants;
import com.saga.notification.kafka.NotificationEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationSenderServiceTest {

    @Mock
    private NotificationEventProducer eventProducer;

    @InjectMocks
    private NotificationSenderService service;

    @Test
    void sendBuildsEventAndCallsProducer() {
        Map<String, Object> payload = Map.of(TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1);
        service.send(payload);
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
        verify(eventProducer).sendSent(eq(TestConstants.TRANSFER_ID_1), eventCaptor.capture());
        assertEquals(TestConstants.TRANSFER_ID_1, eventCaptor.getValue().get(TestConstants.PAYLOAD_KEY_TRANSFER_ID));
        assertTrue((Boolean) eventCaptor.getValue().get(TestConstants.PAYLOAD_KEY_SENT));
    }
}
