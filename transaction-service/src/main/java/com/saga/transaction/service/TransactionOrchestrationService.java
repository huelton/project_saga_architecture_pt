package com.saga.transaction.service;

import com.saga.transaction.entity.TransactionRecord;
import com.saga.transaction.kafka.TransactionEventProducer;
import com.saga.transaction.repository.TransactionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionOrchestrationService {

    private final TransactionRecordRepository repository;
    private final TransactionEventProducer eventProducer;

    public TransactionOrchestrationService(TransactionRecordRepository repository,
                                           TransactionEventProducer eventProducer) {
        this.repository = repository;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public void debit(Map<String, Object> payload) {
        String transferId = (String) payload.get("transferId");
        TransactionRecord record = new TransactionRecord();
        record.setTransferId(transferId);
        record.setType("DEBIT");
        record.setAccountId((String) payload.get("originAccountId"));
        record.setAmount(new BigDecimal(payload.get("amount").toString()));
        record.setCreatedAt(Instant.now());
        repository.save(record);
        Map<String, Object> event = new HashMap<>(payload);
        event.put("transferId", transferId);
        eventProducer.sendDebited(transferId, event);
    }

    @Transactional
    public void credit(Map<String, Object> payload) {
        String transferId = (String) payload.get("transferId");
        TransactionRecord record = new TransactionRecord();
        record.setTransferId(transferId);
        record.setType("CREDIT");
        record.setAccountId((String) payload.get("destinationAccountId"));
        record.setAmount(new BigDecimal(payload.get("amount").toString()));
        record.setCreatedAt(Instant.now());
        repository.save(record);
        Map<String, Object> event = new HashMap<>(payload);
        event.put("transferId", transferId);
        eventProducer.sendCredited(transferId, event);
    }
}
