package com.saga.audit.kafka;

import com.saga.audit.constants.AuditConstants;
import com.saga.audit.service.AuditRecordService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuditCommandConsumer {

    private final AuditRecordService auditRecordService;

    public AuditCommandConsumer(AuditRecordService auditRecordService) {
        this.auditRecordService = auditRecordService;
    }

    @KafkaListener(topics = AuditConstants.TOPIC_AUDIT_RECORD)
    public void onRecord(Map<String, Object> payload) {
        auditRecordService.record(payload);
    }
}
