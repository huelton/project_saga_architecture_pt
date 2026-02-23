package com.saga.validation.service;

import com.saga.validation.document.ComplianceLog;
import com.saga.validation.kafka.ComplianceEventProducer;
import com.saga.validation.repository.ComplianceLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class ComplianceService {

    private final ComplianceLogRepository repository;
    private final ComplianceEventProducer eventProducer;

    public ComplianceService(ComplianceLogRepository repository, ComplianceEventProducer eventProducer) {
        this.repository = repository;
        this.eventProducer = eventProducer;
    }

    public void validate(Map<String, Object> payload) {
        String transferId = (String) payload.get("transferId");
        ComplianceLog log = new ComplianceLog();
        log.setTransferId(transferId);
        log.setCreatedAt(Instant.now());
        log.setStatus("APPROVED");
        repository.save(log);
        Map<String, Object> event = new HashMap<>(payload);
        event.put("transferId", transferId);
        event.put("approved", true);
        eventProducer.sendApproved(transferId, event);
    }
}
