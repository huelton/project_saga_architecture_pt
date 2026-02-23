package com.saga.account.service;

import com.saga.account.entity.Account;
import com.saga.account.kafka.AccountEventProducer;
import com.saga.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountOrchestrationService {

    private final AccountRepository accountRepository;
    private final AccountEventProducer eventProducer;

    public AccountOrchestrationService(AccountRepository accountRepository, AccountEventProducer eventProducer) {
        this.accountRepository = accountRepository;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public void validateOrigin(Map<String, Object> payload) {
        String transferId = (String) payload.get("transferId");
        String accountId = (String) payload.get("originAccountId");
        Map<String, Object> event = new HashMap<>(payload);
        event.put("transferId", transferId);
        boolean valid = accountRepository.findByAccountId(accountId).isPresent();
        event.put("valid", valid);
        eventProducer.sendValidated(transferId, event);
    }

    @Transactional
    public void validateDestination(Map<String, Object> payload) {
        String transferId = (String) payload.get("transferId");
        String accountId = (String) payload.get("destinationAccountId");
        Map<String, Object> event = new HashMap<>(payload);
        event.put("transferId", transferId);
        boolean valid = accountRepository.findByAccountId(accountId).isPresent();
        event.put("valid", valid);
        eventProducer.sendValidated(transferId, event);
    }
}
