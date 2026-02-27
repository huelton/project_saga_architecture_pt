package com.saga.orchestrator.service;

import com.saga.common.dto.SagaEvent;
import com.saga.orchestrator.constants.SagaConstants;
import com.saga.orchestrator.entity.SagaInstance;
import com.saga.orchestrator.kafka.SagaKafkaProducer;
import com.saga.orchestrator.repository.SagaInstanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SagaOrchestratorService {

    private final SagaInstanceRepository repository;
    private final SagaKafkaProducer kafkaProducer;

    public SagaOrchestratorService(SagaInstanceRepository repository, SagaKafkaProducer kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    public SagaInstance startTransfer(String transferId, String originAccountId, String destinationAccountId,
                                      String amount, String currency) {
        SagaInstance instance = new SagaInstance();
        instance.setTransferId(transferId);
        instance.setCurrentState(SagaConstants.STATE_VALIDATING_ORIGIN);
        repository.save(instance);
        SagaEvent event = new SagaEvent(transferId, SagaConstants.STATE_VALIDATING_ORIGIN, null);
        event.setOriginAccountId(originAccountId);
        event.setDestinationAccountId(destinationAccountId);
        event.setAmount(amount);
        event.setCurrency(currency);
        kafkaProducer.sendToAccountValidateOrigin(transferId, event);
        return instance;
    }

    public void handleAccountValidated(SagaEvent event) {
        Optional<SagaInstance> opt = repository.findByTransferId(event.getTransferId());
        opt.ifPresent(inst -> {
            inst.setCurrentState(SagaConstants.STATE_VALIDATING_COMPLIANCE);
            repository.save(inst);
            kafkaProducer.sendToComplianceValidate(event.getTransferId(), event);
        });
    }

    public void handleComplianceApproved(SagaEvent event) {
        Optional<SagaInstance> opt = repository.findByTransferId(event.getTransferId());
        opt.ifPresent(inst -> {
            inst.setCurrentState(SagaConstants.STATE_CONVERTING_CURRENCY);
            repository.save(inst);
            kafkaProducer.sendToCurrencyConvert(event.getTransferId(), event);
        });
    }

    public void handleCurrencyConverted(SagaEvent event) {
        Optional<SagaInstance> opt = repository.findByTransferId(event.getTransferId());
        opt.ifPresent(inst -> {
            inst.setCurrentState(SagaConstants.STATE_DEBITING);
            repository.save(inst);
            kafkaProducer.sendToTransactionDebit(event.getTransferId(), event);
        });
    }

    public void handleTransactionDebited(SagaEvent event) {
        Optional<SagaInstance> opt = repository.findByTransferId(event.getTransferId());
        opt.ifPresent(inst -> {
            inst.setCurrentState(SagaConstants.STATE_CREDITING);
            repository.save(inst);
            kafkaProducer.sendToTransactionCredit(event.getTransferId(), event);
        });
    }

    public void handleTransactionCredited(SagaEvent event) {
        Optional<SagaInstance> opt = repository.findByTransferId(event.getTransferId());
        opt.ifPresent(inst -> {
            inst.setCurrentState(SagaConstants.STATE_COMPLETED);
            repository.save(inst);
        });
    }

    public void handleFailureOrCompensation(SagaEvent event) {
        repository.findByTransferId(event.getTransferId()).ifPresent(inst -> {
            inst.setCurrentState(SagaConstants.STATE_FAILED);
            repository.save(inst);
        });
    }

    public Optional<SagaInstance> getStatus(String transferId) {
        return repository.findByTransferId(transferId);
    }
}
