package com.saga.orchestrator.web;

import com.saga.common.dto.TransferRequest;
import com.saga.common.dto.TransferResponse;
import com.saga.orchestrator.entity.SagaInstance;
import com.saga.orchestrator.service.SagaOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final SagaOrchestratorService orchestratorService;

    public TransferController(SagaOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> initiateTransfer(@RequestBody TransferRequest request) {
        String transferId = UUID.randomUUID().toString();
        SagaInstance instance = orchestratorService.startTransfer(
                transferId,
                request.getOriginAccountId(),
                request.getDestinationAccountId(),
                request.getAmount(),
                request.getCurrency()
        );
        return ResponseEntity.accepted()
                .body(new TransferResponse(instance.getId() != null ? instance.getId() : transferId, "STARTED", "Transfer initiated"));
    }

    @GetMapping("/{transferId}/status")
    public ResponseEntity<TransferResponse> getStatus(@PathVariable String transferId) {
        return orchestratorService.getStatus(transferId)
                .map(inst -> ResponseEntity.ok(new TransferResponse(
                        inst.getTransferId(), inst.getCurrentState(), null)))
                .orElse(ResponseEntity.notFound().build());
    }
}
