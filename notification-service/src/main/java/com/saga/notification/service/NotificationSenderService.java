package com.saga.notification.service;

import com.saga.notification.kafka.NotificationEventProducer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationSenderService {

    private final NotificationEventProducer eventProducer;

    public NotificationSenderService(NotificationEventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    public void send(Map<String, Object> payload) {
        String transferId = (String) payload.get("transferId");
        Map<String, Object> event = new HashMap<>(payload);
        event.put("transferId", transferId);
        event.put("sent", true);
        eventProducer.sendSent(transferId, event);
    }
}
