package com.saga.validation.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ValidationConstantsTest {

    @Test
    void topicsAreDefined() {
        assertNotNull(ValidationConstants.TOPIC_COMPLIANCE_VALIDATE);
        assertNotNull(ValidationConstants.TOPIC_COMPLIANCE_APPROVED);
    }

    @Test
    void topicValuesMatchExpected() {
        assertEquals(TestConstants.EXPECTED_TOPIC_COMPLIANCE_VALIDATE, ValidationConstants.TOPIC_COMPLIANCE_VALIDATE);
        assertEquals(TestConstants.EXPECTED_TOPIC_COMPLIANCE_APPROVED, ValidationConstants.TOPIC_COMPLIANCE_APPROVED);
    }
}
