package com.saga.validation.kafka;

import com.saga.validation.constants.TestConstants;
import com.saga.validation.constants.ValidationConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ComplianceEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ComplianceEventProducer producer;

    @Test
    void sendApprovedSendsToTopicWithTransferIdAndEvent() {
        Map<String, Object> event = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_APPROVED, true
        );
        producer.sendApproved(TestConstants.TRANSFER_ID_1, event);
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), org.mockito.ArgumentMatchers.any());
        assertEquals(ValidationConstants.TOPIC_COMPLIANCE_APPROVED, topicCaptor.getValue());
        assertEquals(TestConstants.TRANSFER_ID_1, keyCaptor.getValue());
    }
}
