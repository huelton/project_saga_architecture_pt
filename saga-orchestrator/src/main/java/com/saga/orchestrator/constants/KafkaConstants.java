package com.saga.orchestrator.constants;

public final class KafkaConstants {

    private KafkaConstants() {}

    // Commands (orchestrator sends)
    public static final String TOPIC_TRANSFER_INITIATE = "transfer.initiate";
    public static final String TOPIC_ACCOUNT_VALIDATE_ORIGIN = "account.validate.origin";
    public static final String TOPIC_ACCOUNT_VALIDATE_DESTINATION = "account.validate.destination";
    public static final String TOPIC_COMPLIANCE_VALIDATE = "compliance.validate";
    public static final String TOPIC_CURRENCY_CONVERT = "currency.convert";
    public static final String TOPIC_TRANSACTION_DEBIT = "transaction.debit";
    public static final String TOPIC_TRANSACTION_CREDIT = "transaction.credit";
    public static final String TOPIC_NOTIFICATION_SEND = "notification.send";
    public static final String TOPIC_AUDIT_RECORD = "audit.record";

    // Events (orchestrator consumes)
    public static final String TOPIC_TRANSFER_STARTED = "transfer.started";
    public static final String TOPIC_ACCOUNT_VALIDATED = "account.validated";
    public static final String TOPIC_COMPLIANCE_APPROVED = "compliance.approved";
    public static final String TOPIC_CURRENCY_CONVERTED = "currency.converted";
    public static final String TOPIC_TRANSACTION_DEBITED = "transaction.debited";
    public static final String TOPIC_TRANSACTION_CREDITED = "transaction.credited";
    public static final String TOPIC_TRANSFER_COMPLETED = "transfer.completed";
    public static final String TOPIC_TRANSFER_FAILED = "transfer.failed";
    public static final String TOPIC_TRANSFER_COMPENSATED = "transfer.compensated";
}
