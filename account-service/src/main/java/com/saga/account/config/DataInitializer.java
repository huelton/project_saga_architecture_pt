package com.saga.account.config;

import com.saga.account.entity.Account;
import com.saga.account.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("!test")
    public CommandLineRunner init(AccountRepository accountRepository) {
        return args -> {
            if (accountRepository.count() == 0) {
                Account a1 = new Account();
                a1.setAccountId("ACC-001");
                a1.setBalance(new BigDecimal("10000.00"));
                a1.setCurrency("USD");
                accountRepository.save(a1);
                Account a2 = new Account();
                a2.setAccountId("ACC-002");
                a2.setBalance(new BigDecimal("5000.00"));
                a2.setCurrency("USD");
                accountRepository.save(a2);
            }
        };
    }
}
