package com.saga.audit.kafka;

import com.saga.audit.constants.AuditConstants;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuditEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuditEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRecorded(String transferId, Map<String, Object> event) {
        kafkaTemplate.send(AuditConstants.TOPIC_AUDIT_RECORDED, transferId, event);
    }
}
