package com.saga.audit.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuditConstantsTest {

    @Test
    void topicsAreDefined() {
        assertNotNull(AuditConstants.TOPIC_AUDIT_RECORD);
        assertNotNull(AuditConstants.TOPIC_AUDIT_RECORDED);
    }

    @Test
    void topicValuesMatchExpected() {
        assertEquals(TestConstants.EXPECTED_TOPIC_AUDIT_RECORD, AuditConstants.TOPIC_AUDIT_RECORD);
        assertEquals(TestConstants.EXPECTED_TOPIC_AUDIT_RECORDED, AuditConstants.TOPIC_AUDIT_RECORDED);
    }
}
