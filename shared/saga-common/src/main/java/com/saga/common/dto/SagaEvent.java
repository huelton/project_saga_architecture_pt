package com.saga.common.dto;

import java.util.Map;

/**
 * Evento SAGA compartilhado entre orquestrador e microsserviços.
 * Usado na mensageria Kafka para propagar estado e dados da transferência.
 */
public class SagaEvent {

    private String transferId;
    private String state;
    private Map<String, Object> payload;
    private String originAccountId;
    private String destinationAccountId;
    private String amount;
    private String currency;

    public SagaEvent() {}

    public SagaEvent(String transferId, String state, Map<String, Object> payload) {
        this.transferId = transferId;
        this.state = state;
        this.payload = payload;
    }

    public String getTransferId() { return transferId; }
    public void setTransferId(String transferId) { this.transferId = transferId; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
    public String getOriginAccountId() { return originAccountId; }
    public void setOriginAccountId(String originAccountId) { this.originAccountId = originAccountId; }
    public String getDestinationAccountId() { return destinationAccountId; }
    public void setDestinationAccountId(String destinationAccountId) { this.destinationAccountId = destinationAccountId; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
