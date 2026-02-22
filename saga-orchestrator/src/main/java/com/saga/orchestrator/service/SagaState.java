package com.saga.orchestrator.service;

import com.saga.orchestrator.constants.SagaConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa o estado atual de uma instância SAGA na memória durante o processamento.
 */
public class SagaState {

    private String transferId;
    private String currentState = SagaConstants.STATE_PENDING;
    private final Map<String, Object> stepResults = new HashMap<>();
    private String failureReason;

    public String getTransferId() { return transferId; }
    public void setTransferId(String transferId) { this.transferId = transferId; }
    public String getCurrentState() { return currentState; }
    public void setCurrentState(String currentState) { this.currentState = currentState; }
    public Map<String, Object> getStepResults() { return stepResults; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
