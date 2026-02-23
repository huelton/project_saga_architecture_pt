package com.saga.account.constants;

import java.math.BigDecimal;

/**
 * Constantes para testes unitários.
 */
public final class TestConstants {

    private TestConstants() {}

    public static final String ACCOUNT_ID_TEST = "ACC-TEST";
    public static final String BALANCE_100 = "100.00";
    public static final BigDecimal BALANCE_100_BD = new BigDecimal(BALANCE_100);
    public static final String CURRENCY_USD = "USD";

    public static final String PAYLOAD_KEY_TRANSFER_ID = "transferId";
    public static final String PAYLOAD_KEY_ORIGIN_ACCOUNT_ID = "originAccountId";
    public static final String PAYLOAD_KEY_DESTINATION_ACCOUNT_ID = "destinationAccountId";
    public static final String PAYLOAD_KEY_VALID = "valid";
    public static final String TRANSFER_ID_1 = "T-001";
    public static final String ORIGIN_ACCOUNT_ID = "ACC-ORIGIN";
    public static final String DESTINATION_ACCOUNT_ID = "ACC-DEST";

    /** Valores esperados dos tópicos (AccountConstants) */
    public static final String EXPECTED_TOPIC_VALIDATE_ORIGIN = "account.validate.origin";
    public static final String EXPECTED_TOPIC_VALIDATED = "account.validated";
    public static final String EXPECTED_TOPIC_DEBIT = "transaction.debit";
    public static final String EXPECTED_TOPIC_CREDITED = "transaction.credited";
}
