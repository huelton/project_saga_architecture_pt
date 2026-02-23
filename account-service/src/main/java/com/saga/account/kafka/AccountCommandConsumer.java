package com.saga.account.kafka;

import com.saga.account.constants.AccountConstants;
import com.saga.account.service.AccountOrchestrationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccountCommandConsumer {

    private final AccountOrchestrationService orchestrationService;

    public AccountCommandConsumer(AccountOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @KafkaListener(topics = AccountConstants.TOPIC_VALIDATE_ORIGIN)
    public void onValidateOrigin(Map<String, Object> payload) {
        orchestrationService.validateOrigin(payload);
    }

    @KafkaListener(topics = AccountConstants.TOPIC_VALIDATE_DESTINATION)
    public void onValidateDestination(Map<String, Object> payload) {
        orchestrationService.validateDestination(payload);
    }
}
