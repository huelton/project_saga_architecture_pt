package com.saga.currency.kafka;

import com.saga.currency.constants.TestConstants;
import com.saga.currency.service.CurrencyConversionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CurrencyCommandConsumerTest {

    @Mock
    private CurrencyConversionService conversionService;

    @InjectMocks
    private CurrencyCommandConsumer consumer;

    @Test
    void onConvertDelegatesToService() {
        Map<String, Object> payload = Map.of(TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1);
        consumer.onConvert(payload);
        verify(conversionService).convert(payload);
    }
}
