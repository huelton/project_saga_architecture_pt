package com.saga.validation.kafka;

import com.saga.validation.constants.TestConstants;
import com.saga.validation.service.ComplianceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ComplianceCommandConsumerTest {

    @Mock
    private ComplianceService complianceService;

    @InjectMocks
    private ComplianceCommandConsumer consumer;

    @Test
    void onValidateDelegatesToService() {
        Map<String, Object> payload = Map.of(TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1);
        consumer.onValidate(payload);
        verify(complianceService).validate(payload);
    }
}
