package com.saga.validation.kafka;

import com.saga.validation.constants.ValidationConstants;
import com.saga.validation.service.ComplianceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ComplianceCommandConsumer {

    private final ComplianceService complianceService;

    public ComplianceCommandConsumer(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @KafkaListener(topics = ValidationConstants.TOPIC_COMPLIANCE_VALIDATE)
    public void onValidate(Map<String, Object> payload) {
        complianceService.validate(payload);
    }
}
