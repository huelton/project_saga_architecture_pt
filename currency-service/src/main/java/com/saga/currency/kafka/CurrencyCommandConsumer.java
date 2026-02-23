package com.saga.currency.kafka;

import com.saga.currency.constants.CurrencyConstants;
import com.saga.currency.service.CurrencyConversionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CurrencyCommandConsumer {

    private final CurrencyConversionService conversionService;

    public CurrencyCommandConsumer(CurrencyConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @KafkaListener(topics = CurrencyConstants.TOPIC_CURRENCY_CONVERT)
    public void onConvert(Map<String, Object> payload) {
        conversionService.convert(payload);
    }
}
