package com.saga.account.service;

import com.saga.account.constants.TestConstants;
import com.saga.account.entity.Account;
import com.saga.account.kafka.AccountEventProducer;
import com.saga.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountOrchestrationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountEventProducer eventProducer;

    @InjectMocks
    private AccountOrchestrationService service;

    @Test
    void validateOriginSendsValidWhenAccountExists() {
        when(accountRepository.findByAccountId(TestConstants.ORIGIN_ACCOUNT_ID))
            .thenReturn(Optional.of(new Account()));
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_ORIGIN_ACCOUNT_ID, TestConstants.ORIGIN_ACCOUNT_ID
        );
        service.validateOrigin(payload);
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
        verify(eventProducer).sendValidated(eq(TestConstants.TRANSFER_ID_1), eventCaptor.capture());
        assertTrue((Boolean) eventCaptor.getValue().get(TestConstants.PAYLOAD_KEY_VALID));
    }

    @Test
    void validateOriginSendsInvalidWhenAccountMissing() {
        when(accountRepository.findByAccountId(TestConstants.ORIGIN_ACCOUNT_ID)).thenReturn(Optional.empty());
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_ORIGIN_ACCOUNT_ID, TestConstants.ORIGIN_ACCOUNT_ID
        );
        service.validateOrigin(payload);
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
        verify(eventProducer).sendValidated(eq(TestConstants.TRANSFER_ID_1), eventCaptor.capture());
        assertEquals(false, eventCaptor.getValue().get(TestConstants.PAYLOAD_KEY_VALID));
    }

    @Test
    void validateDestinationSendsValidWhenAccountExists() {
        when(accountRepository.findByAccountId(TestConstants.DESTINATION_ACCOUNT_ID))
            .thenReturn(Optional.of(new Account()));
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_DESTINATION_ACCOUNT_ID, TestConstants.DESTINATION_ACCOUNT_ID
        );
        service.validateDestination(payload);
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
        verify(eventProducer).sendValidated(eq(TestConstants.TRANSFER_ID_1), eventCaptor.capture());
        assertTrue((Boolean) eventCaptor.getValue().get(TestConstants.PAYLOAD_KEY_VALID));
    }

    @Test
    void validateDestinationSendsInvalidWhenAccountMissing() {
        when(accountRepository.findByAccountId(TestConstants.DESTINATION_ACCOUNT_ID)).thenReturn(Optional.empty());
        Map<String, Object> payload = Map.of(
            TestConstants.PAYLOAD_KEY_TRANSFER_ID, TestConstants.TRANSFER_ID_1,
            TestConstants.PAYLOAD_KEY_DESTINATION_ACCOUNT_ID, TestConstants.DESTINATION_ACCOUNT_ID
        );
        service.validateDestination(payload);
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
        verify(eventProducer).sendValidated(eq(TestConstants.TRANSFER_ID_1), eventCaptor.capture());
        assertEquals(false, eventCaptor.getValue().get(TestConstants.PAYLOAD_KEY_VALID));
    }
}
