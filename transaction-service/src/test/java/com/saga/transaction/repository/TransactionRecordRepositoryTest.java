package com.saga.transaction.repository;

import com.saga.transaction.constants.TestConstants;
import com.saga.transaction.entity.TransactionRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRecordRepositoryTest {

    @Autowired
    private TransactionRecordRepository repository;

    @Test
    void shouldSaveAndFindByTransferId() {
        TransactionRecord record = new TransactionRecord();
        record.setTransferId(TestConstants.TRANSFER_ID_1);
        record.setType(TestConstants.TYPE_DEBIT);
        record.setAccountId(TestConstants.ACCOUNT_ID_1);
        record.setAmount(TestConstants.AMOUNT_100_BD);
        record.setCreatedAt(Instant.now());
        repository.save(record);
        List<TransactionRecord> found = repository.findByTransferId(TestConstants.TRANSFER_ID_1);
        assertEquals(1, found.size());
        assertEquals(TestConstants.TRANSFER_ID_1, found.get(0).getTransferId());
        assertEquals(TestConstants.TYPE_DEBIT, found.get(0).getType());
    }
}
