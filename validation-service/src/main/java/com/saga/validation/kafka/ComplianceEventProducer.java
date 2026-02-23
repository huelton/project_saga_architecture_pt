package com.saga.validation.kafka;

import com.saga.validation.constants.ValidationConstants;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ComplianceEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ComplianceEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendApproved(String transferId, Map<String, Object> event) {
        kafkaTemplate.send(ValidationConstants.TOPIC_COMPLIANCE_APPROVED, transferId, event);
    }
}
