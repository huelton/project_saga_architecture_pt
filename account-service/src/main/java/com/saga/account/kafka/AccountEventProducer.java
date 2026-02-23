package com.saga.account.kafka;

import com.saga.account.constants.AccountConstants;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccountEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AccountEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendValidated(String transferId, Map<String, Object> event) {
        kafkaTemplate.send(AccountConstants.TOPIC_VALIDATED, transferId, event);
    }
}
