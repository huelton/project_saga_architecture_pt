package com.saga.orchestrator.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SagaConstantsTest {

    @Test
    void stateConstantsAreDefined() {
        assertNotNull(SagaConstants.STATE_PENDING);
        assertNotNull(SagaConstants.STATE_VALIDATING_ORIGIN);
        assertNotNull(SagaConstants.STATE_VALIDATING_COMPLIANCE);
        assertNotNull(SagaConstants.STATE_CONVERTING_CURRENCY);
        assertNotNull(SagaConstants.STATE_DEBITING);
        assertNotNull(SagaConstants.STATE_CREDITING);
        assertNotNull(SagaConstants.STATE_COMPLETED);
        assertNotNull(SagaConstants.STATE_FAILED);
    }
}
