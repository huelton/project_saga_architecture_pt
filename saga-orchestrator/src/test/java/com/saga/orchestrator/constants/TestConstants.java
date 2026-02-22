package com.saga.orchestrator.constants;

/**
 * Constantes para testes unitários e de integração.
 */
public final class TestConstants {

    private TestConstants() {}

    public static final String TRANSFER_ID_1 = "T-001";
    public static final String TRANSFER_ID_2 = "T-1";
    public static final String INSTANCE_ID_1 = "id-1";
    public static final String ORIGIN_ACCOUNT_ID = "A1";
    public static final String DESTINATION_ACCOUNT_ID = "A2";
    public static final String AMOUNT_100 = "100";
    public static final String CURRENCY_USD = "USD";
    public static final String API_TRANSFERS = "/api/transfers";
    public static final String PATH_STATUS_SUFFIX = "/status";
    public static final String JSON_ORIGIN = "originAccountId";
    public static final String JSON_DESTINATION = "destinationAccountId";
    public static final String JSON_AMOUNT = "amount";
    public static final String JSON_CURRENCY = "currency";
}
