package com.saga.currency.kafka;

import com.saga.currency.constants.CurrencyConstants;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CurrencyEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CurrencyEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendConverted(String transferId, Map<String, Object> event) {
        kafkaTemplate.send(CurrencyConstants.TOPIC_CURRENCY_CONVERTED, transferId, event);
    }
}
