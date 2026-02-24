package com.saga.transaction.constants;

import java.math.BigDecimal;

/**
 * Constantes para testes unitários.
 */
public final class TestConstants {

    private TestConstants() {}

    public static final String TRANSFER_ID_1 = "T-1";
    public static final String ACCOUNT_ID_1 = "ACC-1";
    public static final String ACCOUNT_ID_2 = "ACC-2";
    public static final String AMOUNT_100 = "100";
    public static final BigDecimal AMOUNT_100_BD = new BigDecimal(AMOUNT_100);
    public static final String TYPE_DEBIT = "DEBIT";
    public static final String TYPE_CREDIT = "CREDIT";

    public static final String PAYLOAD_KEY_TRANSFER_ID = "transferId";
    public static final String PAYLOAD_KEY_ORIGIN_ACCOUNT_ID = "originAccountId";
    public static final String PAYLOAD_KEY_DESTINATION_ACCOUNT_ID = "destinationAccountId";
    public static final String PAYLOAD_KEY_AMOUNT = "amount";

    public static final String EXPECTED_TOPIC_DEBIT = "transaction.debit";
    public static final String EXPECTED_TOPIC_CREDIT = "transaction.credit";
    public static final String EXPECTED_TOPIC_DEBITED = "transaction.debited";
    public static final String EXPECTED_TOPIC_CREDITED = "transaction.credited";
}
