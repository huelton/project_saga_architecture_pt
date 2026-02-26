package com.saga.audit.kafka;

import com.saga.audit.constants.AuditConstants;
import com.saga.audit.constants.TestConstants;
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
class AuditEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AuditEventProducer producer;

    @Test
    void sendRecordedSendsToTopicWithTransferIdAndEvent() {
        Map<String, Object> event = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_RECORDED, true
        );
        producer.sendRecorded(TestConstants.TRANSFER_ID_1, event);
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());
        assertEquals(AuditConstants.TOPIC_AUDIT_RECORDED, topicCaptor.getValue());
        assertEquals(TestConstants.TRANSFER_ID_1, keyCaptor.getValue());
    }
}
