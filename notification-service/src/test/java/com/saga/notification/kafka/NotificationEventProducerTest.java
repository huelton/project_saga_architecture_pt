package com.saga.notification.kafka;

import com.saga.notification.constants.NotificationConstants;
import com.saga.notification.constants.TestConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private NotificationEventProducer producer;

    @Test
    void sendSentSendsToTopicWithTransferIdAndEvent() {
        Map<String, Object> event = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_SENT, true
        );
        producer.sendSent(TestConstants.TRANSFER_ID_1, event);
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), org.mockito.ArgumentMatchers.any());
        assertEquals(NotificationConstants.TOPIC_NOTIFICATION_SENT, topicCaptor.getValue());
        assertEquals(TestConstants.TRANSFER_ID_1, keyCaptor.getValue());
    }
}
