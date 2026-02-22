package com.saga.orchestrator.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConstantsTest {

    @Test
    void commandTopicsAreDefined() {
        assertNotNull(KafkaConstants.TOPIC_TRANSFER_INITIATE);
        assertNotNull(KafkaConstants.TOPIC_ACCOUNT_VALIDATE_ORIGIN);
        assertNotNull(KafkaConstants.TOPIC_ACCOUNT_VALIDATE_DESTINATION);
        assertNotNull(KafkaConstants.TOPIC_COMPLIANCE_VALIDATE);
        assertNotNull(KafkaConstants.TOPIC_CURRENCY_CONVERT);
        assertNotNull(KafkaConstants.TOPIC_TRANSACTION_DEBIT);
        assertNotNull(KafkaConstants.TOPIC_TRANSACTION_CREDIT);
    }

    @Test
    void eventTopicsAreDefined() {
        assertNotNull(KafkaConstants.TOPIC_ACCOUNT_VALIDATED);
        assertNotNull(KafkaConstants.TOPIC_TRANSFER_COMPLETED);
        assertNotNull(KafkaConstants.TOPIC_TRANSFER_FAILED);
    }
}
