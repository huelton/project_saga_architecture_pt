package com.saga.notification.kafka;

import com.saga.notification.constants.NotificationConstants;
import com.saga.notification.service.NotificationSenderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationCommandConsumer {

    private final NotificationSenderService senderService;

    public NotificationCommandConsumer(NotificationSenderService senderService) {
        this.senderService = senderService;
    }

    @KafkaListener(topics = NotificationConstants.TOPIC_NOTIFICATION_SEND)
    public void onSend(Map<String, Object> payload) {
        senderService.send(payload);
    }
}
