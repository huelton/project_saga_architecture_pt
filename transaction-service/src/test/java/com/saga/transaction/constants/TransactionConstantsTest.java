package com.saga.transaction.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionConstantsTest {

    @Test
    void topicsAreDefined() {
        assertNotNull(TransactionConstants.TOPIC_DEBIT);
        assertNotNull(TransactionConstants.TOPIC_CREDIT);
        assertNotNull(TransactionConstants.TOPIC_DEBITED);
        assertNotNull(TransactionConstants.TOPIC_CREDITED);
    }

    @Test
    void topicValuesMatchExpected() {
        assertEquals(TestConstants.EXPECTED_TOPIC_DEBIT, TransactionConstants.TOPIC_DEBIT);
        assertEquals(TestConstants.EXPECTED_TOPIC_CREDIT, TransactionConstants.TOPIC_CREDIT);
        assertEquals(TestConstants.EXPECTED_TOPIC_DEBITED, TransactionConstants.TOPIC_DEBITED);
        assertEquals(TestConstants.EXPECTED_TOPIC_CREDITED, TransactionConstants.TOPIC_CREDITED);
    }
}
