package com.saga.notification.kafka;

import com.saga.notification.constants.NotificationConstants;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotificationEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSent(String transferId, Map<String, Object> event) {
        kafkaTemplate.send(NotificationConstants.TOPIC_NOTIFICATION_SENT, transferId, event);
    }
}
