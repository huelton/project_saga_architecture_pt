package com.saga.orchestrator.kafka;

import com.saga.orchestrator.constants.KafkaConstants;
import com.saga.common.dto.SagaEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SagaKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToAccountValidateOrigin(String key, Object payload) {
        kafkaTemplate.send(KafkaConstants.TOPIC_ACCOUNT_VALIDATE_ORIGIN, key, payload);
    }

    public void sendToAccountValidateDestination(String key, Object payload) {
        kafkaTemplate.send(KafkaConstants.TOPIC_ACCOUNT_VALIDATE_DESTINATION, key, payload);
    }

    public void sendToComplianceValidate(String key, Object payload) {
        kafkaTemplate.send(KafkaConstants.TOPIC_COMPLIANCE_VALIDATE, key, payload);
    }

    public void sendToCurrencyConvert(String key, Object payload) {
        kafkaTemplate.send(KafkaConstants.TOPIC_CURRENCY_CONVERT, key, payload);
    }

    public void sendToTransactionDebit(String key, Object payload) {
        kafkaTemplate.send(KafkaConstants.TOPIC_TRANSACTION_DEBIT, key, payload);
    }

    public void sendToTransactionCredit(String key, Object payload) {
        kafkaTemplate.send(KafkaConstants.TOPIC_TRANSACTION_CREDIT, key, payload);
    }

    public void sendSagaEvent(String topic, String key, SagaEvent event) {
        kafkaTemplate.send(topic, key, event);
    }
}
