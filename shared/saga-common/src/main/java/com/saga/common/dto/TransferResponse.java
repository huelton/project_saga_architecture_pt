package com.saga.common.dto;

/**
 * Contrato de resposta da API de transferências.
 * Retornado em POST /api/transfers e GET /api/transfers/{id}/status.
 */
public class TransferResponse {

    private String transferId;
    private String status;
    private String message;

    public TransferResponse() {}

    public TransferResponse(String transferId, String status, String message) {
        this.transferId = transferId;
        this.status = status;
        this.message = message;
    }

    public String getTransferId() { return transferId; }
    public void setTransferId(String transferId) { this.transferId = transferId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
