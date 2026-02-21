package com.saga.common.dto;

/**
 * Contrato de requisição para iniciar uma transferência.
 * Usado pela API do orquestrador (POST /api/transfers).
 */
public class TransferRequest {

    private String originAccountId;
    private String destinationAccountId;
    private String amount;
    private String currency;

    public String getOriginAccountId() { return originAccountId; }
    public void setOriginAccountId(String originAccountId) { this.originAccountId = originAccountId; }
    public String getDestinationAccountId() { return destinationAccountId; }
    public void setDestinationAccountId(String destinationAccountId) { this.destinationAccountId = destinationAccountId; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
