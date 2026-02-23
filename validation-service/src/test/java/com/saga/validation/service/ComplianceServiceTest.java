package com.saga.validation.service;

import com.saga.validation.constants.ValidationConstants;
import com.saga.validation.constants.TestConstants;
import com.saga.validation.kafka.ComplianceEventProducer;
import com.saga.validation.repository.ComplianceLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ComplianceServiceTest {

    @Mock
    private ComplianceLogRepository repository;

    @Mock
    private ComplianceEventProducer eventProducer;

    @InjectMocks
    private ComplianceService service;

    @Test
    void validateSavesLogAndSendsApproved() {
        Map<String, Object> payload = Map.of(TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1);
        service.validate(payload);
        ArgumentCaptor<com.saga.validation.document.ComplianceLog> logCaptor =
            ArgumentCaptor.forClass(com.saga.validation.document.ComplianceLog.class);
        verify(repository).save(logCaptor.capture());
        assertEquals(TestConstants.TRANSFER_ID_1, logCaptor.getValue().getTransferId());
        assertEquals(ValidationConstants.STATUS_APPROVED, logCaptor.getValue().getStatus());
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
        verify(eventProducer).sendApproved(eq(TestConstants.TRANSFER_ID_1), eventCaptor.capture());
        assertTrue((Boolean) eventCaptor.getValue().get(TestConstants.PAYLOAD_KEY_APPROVED));
    }
}
