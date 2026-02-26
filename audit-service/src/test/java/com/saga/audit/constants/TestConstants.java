package com.saga.audit.constants;

/**
 * Constantes para testes unitários.
 */
public final class TestConstants {

    private TestConstants() {}

    public static final String TRANSFER_ID_1 = "T-001";

    /** Chaves de payload/event para testes */
    public static final String PAYLOAD_KEY_TRANSFER_ID = "transferId";
    public static final String PAYLOAD_KEY_AMOUNT = "amount";
    public static final String PAYLOAD_KEY_RECORDED = "recorded";

    public static final String AMOUNT_100 = "100";
    public static final String ACTION_TRANSFER = "TRANSFER";

    /** Valores esperados das constantes de tópico (AuditConstants) */
    public static final String EXPECTED_TOPIC_AUDIT_RECORD = "audit.record";
    public static final String EXPECTED_TOPIC_AUDIT_RECORDED = "audit.recorded";
}
