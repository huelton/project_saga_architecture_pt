package com.saga.currency.service;

import com.saga.currency.constants.CurrencyConstants;
import com.saga.currency.constants.TestConstants;
import com.saga.currency.kafka.CurrencyEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyConversionServiceTest {

    @Mock
    private RedisTemplate<String, Double> rateRedisTemplate;

    @Mock
    private ValueOperations<String, Double> valueOperations;

    @Mock
    private CurrencyEventProducer eventProducer;

    @InjectMocks
    private CurrencyConversionService service;

    @Test
    void convertUsesRedisRateAndSendsEvent() {
        when(rateRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(0.95);
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_CURRENCY, TestConstants.CURRENCY_USD,
            TestConstants.PAYLOAD_KEY_TARGET_CURRENCY, TestConstants.CURRENCY_EUR,
            TestConstants.PAYLOAD_KEY_AMOUNT, "100"
        );
        service.convert(payload);
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
        verify(eventProducer).sendConverted(eq(TestConstants.TRANSFER_ID_1), eventCaptor.capture());
        assertEquals(0.95, eventCaptor.getValue().get(TestConstants.EVENT_KEY_RATE));
    }

    @Test
    void convertUsesDefaultRateWhenRedisReturnsNull() {
        when(rateRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        Map<String, Object> payload = Map.of(TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1);
        service.convert(payload);
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
        verify(eventProducer).sendConverted(eq(TestConstants.TRANSFER_ID_1), eventCaptor.capture());
        assertEquals(0.92, eventCaptor.getValue().get(TestConstants.EVENT_KEY_RATE));
    }
}
