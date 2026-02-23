package com.saga.account.constants;

public final class AccountConstants {

    private AccountConstants() {}

    public static final String TOPIC_VALIDATE_ORIGIN = "account.validate.origin";
    public static final String TOPIC_VALIDATE_DESTINATION = "account.validate.destination";
    public static final String TOPIC_VALIDATED = "account.validated";
    public static final String TOPIC_DEBIT = "transaction.debit";
    public static final String TOPIC_CREDIT = "transaction.credit";
    public static final String TOPIC_DEBITED = "transaction.debited";
    public static final String TOPIC_CREDITED = "transaction.credited";
}
