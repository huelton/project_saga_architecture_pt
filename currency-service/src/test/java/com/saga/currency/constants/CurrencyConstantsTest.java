package com.saga.currency.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CurrencyConstantsTest {

    @Test
    void topicsAreDefined() {
        assertNotNull(CurrencyConstants.TOPIC_CURRENCY_CONVERT);
        assertNotNull(CurrencyConstants.TOPIC_CURRENCY_CONVERTED);
        assertNotNull(CurrencyConstants.REDIS_RATE_PREFIX);
    }

    @Test
    void topicValuesMatchExpected() {
        assertEquals(TestConstants.EXPECTED_TOPIC_CONVERT, CurrencyConstants.TOPIC_CURRENCY_CONVERT);
        assertEquals(TestConstants.EXPECTED_TOPIC_CONVERTED, CurrencyConstants.TOPIC_CURRENCY_CONVERTED);
    }
}
