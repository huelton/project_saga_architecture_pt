package com.saga.orchestrator.kafka;

import com.saga.orchestrator.constants.KafkaConstants;
import com.saga.orchestrator.constants.TestConstants;
import com.saga.common.dto.SagaEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SagaKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private SagaKafkaProducer producer;

    @Test
    void sendToAccountValidateOriginSendsToCorrectTopic() {
        SagaEvent event = new SagaEvent();
        event.setTransferId(TestConstants.TRANSFER_ID_1);
        producer.sendToAccountValidateOrigin(TestConstants.TRANSFER_ID_1, event);
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), org.mockito.ArgumentMatchers.any());
        assertEquals(KafkaConstants.TOPIC_ACCOUNT_VALIDATE_ORIGIN, topicCaptor.getValue());
        assertEquals(TestConstants.TRANSFER_ID_1, keyCaptor.getValue());
    }

    @Test
    void sendToComplianceValidateSendsToCorrectTopic() {
        Object payload = new Object();
        producer.sendToComplianceValidate(TestConstants.TRANSFER_ID_1, payload);
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        assertEquals(KafkaConstants.TOPIC_COMPLIANCE_VALIDATE, topicCaptor.getValue());
    }

    @Test
    void sendToCurrencyConvertSendsToCorrectTopic() {
        producer.sendToCurrencyConvert(TestConstants.TRANSFER_ID_1, new Object());
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        assertEquals(KafkaConstants.TOPIC_CURRENCY_CONVERT, topicCaptor.getValue());
    }

    @Test
    void sendToTransactionDebitSendsToCorrectTopic() {
        producer.sendToTransactionDebit(TestConstants.TRANSFER_ID_1, new Object());
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        assertEquals(KafkaConstants.TOPIC_TRANSACTION_DEBIT, topicCaptor.getValue());
    }

    @Test
    void sendToTransactionCreditSendsToCorrectTopic() {
        producer.sendToTransactionCredit(TestConstants.TRANSFER_ID_1, new Object());
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        assertEquals(KafkaConstants.TOPIC_TRANSACTION_CREDIT, topicCaptor.getValue());
    }

    @Test
    void sendToAccountValidateDestinationSendsToCorrectTopic() {
        producer.sendToAccountValidateDestination(TestConstants.TRANSFER_ID_1, new Object());
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        assertEquals(KafkaConstants.TOPIC_ACCOUNT_VALIDATE_DESTINATION, topicCaptor.getValue());
    }

    @Test
    void sendSagaEventSendsToGivenTopicAndKey() {
        SagaEvent event = new SagaEvent();
        event.setTransferId(TestConstants.TRANSFER_ID_1);
        String topic = "custom-topic";
        producer.sendSagaEvent(topic, TestConstants.TRANSFER_ID_1, event);
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), org.mockito.ArgumentMatchers.any());
        assertEquals(topic, topicCaptor.getValue());
        assertEquals(TestConstants.TRANSFER_ID_1, keyCaptor.getValue());
    }
}
