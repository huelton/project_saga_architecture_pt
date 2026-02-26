package com.saga.audit.kafka;

import com.saga.audit.constants.TestConstants;
import com.saga.audit.service.AuditRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditCommandConsumerTest {

    @Mock
    private AuditRecordService auditRecordService;

    @InjectMocks
    private AuditCommandConsumer consumer;

    @Test
    void onRecordDelegatesToService() {
        Map<String, Object> payload = Map.of(TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1);
        consumer.onRecord(payload);
        verify(auditRecordService).record(payload);
    }
}
