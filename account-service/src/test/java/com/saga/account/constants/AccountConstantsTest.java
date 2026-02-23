package com.saga.account.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountConstantsTest {

    @Test
    void topicsAreDefined() {
        assertNotNull(AccountConstants.TOPIC_VALIDATE_ORIGIN);
        assertNotNull(AccountConstants.TOPIC_VALIDATED);
        assertNotNull(AccountConstants.TOPIC_DEBIT);
        assertNotNull(AccountConstants.TOPIC_CREDITED);
    }

    @Test
    void topicValuesMatchExpected() {
        assertEquals(TestConstants.EXPECTED_TOPIC_VALIDATE_ORIGIN, AccountConstants.TOPIC_VALIDATE_ORIGIN);
        assertEquals(TestConstants.EXPECTED_TOPIC_VALIDATED, AccountConstants.TOPIC_VALIDATED);
        assertEquals(TestConstants.EXPECTED_TOPIC_DEBIT, AccountConstants.TOPIC_DEBIT);
        assertEquals(TestConstants.EXPECTED_TOPIC_CREDITED, AccountConstants.TOPIC_CREDITED);
    }
}
