package com.saga.audit.service;

import com.saga.audit.constants.TestConstants;
import com.saga.audit.kafka.AuditEventProducer;
import com.saga.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditRecordServiceTest {

    @Mock
    private AuditLogRepository repository;

    @Mock
    private AuditEventProducer eventProducer;

    @InjectMocks
    private AuditRecordService service;

    @Test
    void recordSavesLogAndSendsEvent() {
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_AMOUNT, TestConstants.AMOUNT_100
        );
        service.record(payload);
        ArgumentCaptor<com.saga.audit.document.AuditLog> logCaptor =
            ArgumentCaptor.forClass(com.saga.audit.document.AuditLog.class);
        verify(repository).save(logCaptor.capture());
        assertEquals(TestConstants.TRANSFER_ID_1, logCaptor.getValue().getTransferId());
        assertEquals(TestConstants.ACTION_TRANSFER, logCaptor.getValue().getAction());
        verify(eventProducer).sendRecorded(eq(TestConstants.TRANSFER_ID_1), any(Map.class));
    }
}
