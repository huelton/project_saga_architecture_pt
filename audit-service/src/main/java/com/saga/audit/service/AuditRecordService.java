package com.saga.audit.service;

import com.saga.audit.document.AuditLog;
import com.saga.audit.kafka.AuditEventProducer;
import com.saga.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuditRecordService {

    private final AuditLogRepository repository;
    private final AuditEventProducer eventProducer;

    public AuditRecordService(AuditLogRepository repository, AuditEventProducer eventProducer) {
        this.repository = repository;
        this.eventProducer = eventProducer;
    }

    public void record(Map<String, Object> payload) {
        String transferId = (String) payload.get("transferId");
        AuditLog log = new AuditLog();
        log.setTransferId(transferId);
        log.setAction("TRANSFER");
        log.setPayload(payload);
        log.setCreatedAt(Instant.now());
        repository.save(log);
        Map<String, Object> event = new HashMap<>(payload);
        event.put("transferId", transferId);
        event.put("recorded", true);
        eventProducer.sendRecorded(transferId, event);
    }
}
