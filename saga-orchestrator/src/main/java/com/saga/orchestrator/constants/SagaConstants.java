package com.saga.orchestrator.constants;

public final class SagaConstants {

    private SagaConstants() {}

    public static final String STATE_PENDING = "PENDING";
    public static final String STATE_VALIDATING_ORIGIN = "VALIDATING_ORIGIN";
    public static final String STATE_VALIDATING_DESTINATION = "VALIDATING_DESTINATION";
    public static final String STATE_VALIDATING_COMPLIANCE = "VALIDATING_COMPLIANCE";
    public static final String STATE_CONVERTING_CURRENCY = "CONVERTING_CURRENCY";
    public static final String STATE_DEBITING = "DEBITING";
    public static final String STATE_CREDITING = "CREDITING";
    public static final String STATE_NOTIFYING = "NOTIFYING";
    public static final String STATE_AUDITING = "AUDITING";
    public static final String STATE_COMPLETED = "COMPLETED";
    public static final String STATE_COMPENSATING = "COMPENSATING";
    public static final String STATE_FAILED = "FAILED";
}
