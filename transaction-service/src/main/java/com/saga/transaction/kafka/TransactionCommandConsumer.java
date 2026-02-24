package com.saga.transaction.kafka;

import com.saga.transaction.constants.TransactionConstants;
import com.saga.transaction.service.TransactionOrchestrationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransactionCommandConsumer {

    private final TransactionOrchestrationService orchestrationService;

    public TransactionCommandConsumer(TransactionOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @KafkaListener(topics = TransactionConstants.TOPIC_DEBIT)
    public void onDebit(Map<String, Object> payload) {
        orchestrationService.debit(payload);
    }

    @KafkaListener(topics = TransactionConstants.TOPIC_CREDIT)
    public void onCredit(Map<String, Object> payload) {
        orchestrationService.credit(payload);
    }
}
