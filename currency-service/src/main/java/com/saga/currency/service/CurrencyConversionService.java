package com.saga.currency.service;

import com.saga.currency.constants.CurrencyConstants;
import com.saga.currency.kafka.CurrencyEventProducer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyConversionService {

    private final RedisTemplate<String, Double> rateRedisTemplate;
    private final CurrencyEventProducer eventProducer;

    public CurrencyConversionService(RedisTemplate<String, Double> rateRedisTemplate,
                                     CurrencyEventProducer eventProducer) {
        this.rateRedisTemplate = rateRedisTemplate;
        this.eventProducer = eventProducer;
    }

    public void convert(Map<String, Object> payload) {
        String transferId = (String) payload.get("transferId");
        String from = (String) payload.getOrDefault("currency", "USD");
        String to = (String) payload.getOrDefault("targetCurrency", "EUR");
        Double rate = rateRedisTemplate.opsForValue().get(CurrencyConstants.REDIS_RATE_PREFIX + from + ":" + to);
        if (rate == null) rate = 0.92;
        Map<String, Object> event = new HashMap<>(payload);
        event.put("transferId", transferId);
        event.put("convertedAmount", payload.get("amount"));
        event.put("rate", rate);
        eventProducer.sendConverted(transferId, event);
    }
}
