package com.saga.transaction.repository;

import com.saga.transaction.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, String> {

    List<TransactionRecord> findByTransferId(String transferId);
}
