package com.saga.account.kafka;

import com.saga.account.constants.AccountConstants;
import com.saga.account.constants.TestConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AccountEventProducer producer;

    @Test
    void sendValidatedSendsToTopicWithTransferIdAndEvent() {
        Map<String, Object> event = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_VALID, true
        );
        producer.sendValidated(TestConstants.TRANSFER_ID_1, event);
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), any());
        assertEquals(AccountConstants.TOPIC_VALIDATED, topicCaptor.getValue());
        assertEquals(TestConstants.TRANSFER_ID_1, keyCaptor.getValue());
    }
}
