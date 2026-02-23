package com.saga.account.repository;

import com.saga.account.constants.TestConstants;
import com.saga.account.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @Test
    void shouldSaveAndFindByAccountId() {
        Account account = new Account();
        account.setAccountId(TestConstants.ACCOUNT_ID_TEST);
        account.setBalance(TestConstants.BALANCE_100_BD);
        account.setCurrency(TestConstants.CURRENCY_USD);
        repository.save(account);
        Optional<Account> found = repository.findByAccountId(TestConstants.ACCOUNT_ID_TEST);
        assertTrue(found.isPresent());
        assertEquals(TestConstants.ACCOUNT_ID_TEST, found.get().getAccountId());
        assertEquals(TestConstants.CURRENCY_USD, found.get().getCurrency());
    }
}
