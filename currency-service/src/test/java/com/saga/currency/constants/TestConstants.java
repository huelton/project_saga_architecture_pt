package com.saga.currency.constants;

/**
 * Constantes para testes unitários.
 */
public final class TestConstants {

    private TestConstants() {}

    public static final String TRANSFER_ID_1 = "T-001";
    public static final String PAYLOAD_KEY_TRANSFER_ID = "transferId";
    public static final String PAYLOAD_KEY_CURRENCY = "currency";
    public static final String PAYLOAD_KEY_TARGET_CURRENCY = "targetCurrency";
    public static final String PAYLOAD_KEY_AMOUNT = "amount";
    public static final String CURRENCY_USD = "USD";
    public static final String CURRENCY_EUR = "EUR";

    public static final String EXPECTED_TOPIC_CONVERT = "currency.convert";
    public static final String EXPECTED_TOPIC_CONVERTED = "currency.converted";

    public static final String EVENT_KEY_RATE = "rate";
}
