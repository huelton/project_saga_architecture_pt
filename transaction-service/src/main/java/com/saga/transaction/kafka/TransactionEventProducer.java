package com.saga.transaction.kafka;

import com.saga.transaction.constants.TransactionConstants;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransactionEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransactionEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDebited(String transferId, Map<String, Object> event) {
        kafkaTemplate.send(TransactionConstants.TOPIC_DEBITED, transferId, event);
    }

    public void sendCredited(String transferId, Map<String, Object> event) {
        kafkaTemplate.send(TransactionConstants.TOPIC_CREDITED, transferId, event);
    }
}
