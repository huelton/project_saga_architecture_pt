package com.saga.currency.constants;

public final class CurrencyConstants {

    private CurrencyConstants() {}

    public static final String TOPIC_CURRENCY_CONVERT = "currency.convert";
    public static final String TOPIC_CURRENCY_CONVERTED = "currency.converted";
    public static final String REDIS_RATE_PREFIX = "rate:";
}
